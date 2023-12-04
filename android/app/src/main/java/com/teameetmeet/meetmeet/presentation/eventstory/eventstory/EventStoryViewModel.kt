package com.teameetmeet.meetmeet.presentation.eventstory.eventstory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.data.ExpiredRefreshTokenException
import com.teameetmeet.meetmeet.data.repository.EventStoryRepository
import com.teameetmeet.meetmeet.presentation.model.EventAuthority
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class EventStoryViewModel @Inject constructor(
    private val eventStoryRepository: EventStoryRepository
) : ViewModel(), OnItemClickListener, OnNotiChangeListener {

    private val _eventStoryUiState = MutableStateFlow<EventStoryUiState>(EventStoryUiState())
    val eventStoryUiState: StateFlow<EventStoryUiState> = _eventStoryUiState

    private val _event = MutableSharedFlow<EventStoryEvent>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val event : SharedFlow<EventStoryEvent> = _event

    fun setEventId(eventId: Int?) {
        _eventStoryUiState.update {
            it.copy(eventId = eventId?:0)
        }
    }

    fun getStory() {
        viewModelScope.launch {
            eventStoryRepository.getEventStory(eventStoryUiState.value.eventId).onStart {
                _eventStoryUiState.update {
                    it.copy(isLoading = true)
                }
            }.catch {
                _eventStoryUiState.update { uiState ->
                    uiState.copy(isLoading = false)
                }
                when(it) {
                    is ExpiredRefreshTokenException -> {
                        _event.emit(EventStoryEvent.NavigateToLoginActivity)
                    }
                    is UnknownHostException -> {
                        _event.emit(EventStoryEvent.ShowMessage(R.string.common_message_no_internet))
                    }
                    else -> {
                        _event.emit(EventStoryEvent.ShowMessage(R.string.event_story_message_event_story_fail, it.message.orEmpty()))
                    }
                }
            }.collect { eventStory ->
                _eventStoryUiState.update {
                    it.copy(eventStory = eventStory, isLoading = false, authority =
                        when(eventStory.authority) {
                            "OWNER" -> EventAuthority.OWNER
                            "MEMBER" -> EventAuthority.PARTICIPANT
                            else -> EventAuthority.GUEST
                        }
                    )
                }
            }
        }
    }

    fun getNoti(): String {
        return eventStoryUiState.value.eventStory?.announcement.orEmpty()
    }

    fun joinEventStory() {
        viewModelScope.launch {
            eventStoryRepository.joinEventStory(eventStoryUiState.value.eventId).catch { exception ->
                when(exception) {
                    is ExpiredRefreshTokenException -> _event.emit(EventStoryEvent.NavigateToLoginActivity)
                    is UnknownHostException -> _event.emit(EventStoryEvent.ShowMessage(R.string.common_message_no_internet))
                    else -> _event.emit(EventStoryEvent.ShowMessage(R.string.event_story_message_join_event_fail, exception.message.orEmpty()))
                }
            }.collect {
                _event.emit(EventStoryEvent.ShowMessage(R.string.event_story_message_join_event_success))
            }
        }
    }


    override fun onItemClick() {
        _eventStoryUiState.update {
            if(eventStoryUiState.value.isEventMemberUiExpanded) {
                it.copy(isEventMemberUiExpanded = false, maxMember = SHRINK_MAX_MEMBER)
            } else {
                it.copy(isEventMemberUiExpanded = true, maxMember = EXPANDED_MAX_MEMBER)
            }
        }
    }


    override fun onSaveButtonClick(message: String) {
        viewModelScope.launch {
            eventStoryRepository.editNotification(eventStoryUiState.value.eventId, message).catch {
                _event.emit(EventStoryEvent.ShowMessage(R.string.event_story_message_edit_noti_fail, it.message.orEmpty()))
            }.collect {
                _eventStoryUiState.update {
                    it.copy(eventStory = eventStoryUiState.value.eventStory?.copy(announcement = message))
                }
            }
        }
    }
}