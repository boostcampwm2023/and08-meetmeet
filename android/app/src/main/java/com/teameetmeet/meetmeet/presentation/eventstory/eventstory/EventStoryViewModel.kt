package com.teameetmeet.meetmeet.presentation.eventstory.eventstory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.data.repository.EventStoryRepository
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
import javax.inject.Inject

@HiltViewModel
class EventStoryViewModel @Inject constructor(
    private val eventStoryRepository: EventStoryRepository
) : ViewModel(), OnItemClickListener {

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
                    it.copy(eventStory = eventStory, isLoading = false)
                }
            }
        }
    }

    override fun onItemClick() {
        _eventStoryUiState.update {
            it.copy(isEventMemberUiExpanded = eventStoryUiState.value.isEventMemberUiExpanded.not())
        }
    }
}