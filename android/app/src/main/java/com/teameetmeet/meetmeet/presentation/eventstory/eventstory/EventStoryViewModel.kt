package com.teameetmeet.meetmeet.presentation.eventstory.eventstory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.data.repository.EventStoryRepository
import com.teameetmeet.meetmeet.presentation.eventstory.eventstory.adapter.EventFeedListAdapter
import com.teameetmeet.meetmeet.presentation.eventstory.eventstory.adapter.EventMemberListAdapter
import com.teameetmeet.meetmeet.presentation.model.EventAuthority
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventStoryViewModel @Inject constructor(
    private val eventStoryRepository: EventStoryRepository
) : ViewModel(), OnItemClickListener, OnNotiChangeListener {

    private val _eventStoryUiState = MutableStateFlow<EventStoryUiState>(EventStoryUiState())
    val eventStoryUiState: StateFlow<EventStoryUiState> = _eventStoryUiState

    private val _event = MutableSharedFlow<EventStoryEvent>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val event : SharedFlow<EventStoryEvent> = _event

    fun getStory(id: Int) {
        viewModelScope.launch {
            eventStoryRepository.getEventStory(id).onStart {
                _eventStoryUiState.update {
                    it.copy(isLoading = true)
                }
            }.catch {
                _event.emit(EventStoryEvent.ShowMessage(R.string.event_story_message_event_story_fail, it.message.orEmpty()))
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

    override fun onItemClick(viewHolder: RecyclerView.ViewHolder) {
        when(viewHolder) {
            is EventFeedListAdapter.EventFeedViewHolder -> {
                _event.tryEmit(EventStoryEvent.NavigateToFeedFragment)
            }
            is EventMemberListAdapter.EventMemberViewHolder -> {
                _eventStoryUiState.update {
                    if(eventStoryUiState.value.isEventMemberUiExpanded) {
                        it.copy(isEventMemberUiExpanded = false, maxMember = SHRINK_MAX_MEMBER)
                    } else {
                        it.copy(isEventMemberUiExpanded = true, maxMember = EXPANDED_MAX_MEMBER)
                    }
                }
            }
        }
    }

    override fun onSaveButtonClick(message: String) {
        viewModelScope.launch {
            eventStoryRepository.editNotification(message).catch {
                _event.emit(EventStoryEvent.ShowMessage(R.string.event_story_message_edit_noti_fail, it.message.orEmpty()))
            }.collect {
                _eventStoryUiState.update {
                    it.copy(eventStoryUiState.value.eventStory?.copy(announcement = message))
                }
                //TODO("일정 공지 수정되는 거 봐야함")
            }
        }
    }
}