package com.teameetmeet.meetmeet.presentation.eventstory.eventstorydetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.data.repository.EventStoryRepository
import com.teameetmeet.meetmeet.presentation.model.EventAuthority
import com.teameetmeet.meetmeet.presentation.model.EventNotification
import com.teameetmeet.meetmeet.presentation.model.EventRepeatTerm
import com.teameetmeet.meetmeet.presentation.model.EventTime
import com.teameetmeet.meetmeet.util.toDateStringFormat
import com.teameetmeet.meetmeet.util.toTimeStampLong
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class EventStoryDetailViewModel @Inject constructor(
    private val eventStoryRepository: EventStoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<EventStoryDetailUiState>(EventStoryDetailUiState())
    val uiState: StateFlow<EventStoryDetailUiState> = _uiState

    private val _event = MutableSharedFlow<EventStoryDetailEvent>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val event : SharedFlow<EventStoryDetailEvent> = _event


    fun fetchStoryDetail(storyId: Int) {
        viewModelScope.launch {
            eventStoryRepository.getEventStoryDetail(storyId).catch {
                _event.tryEmit(EventStoryDetailEvent.ShowMessage(R.string.story_detail_message_story_detail_fetch_fail, it.message.orEmpty()))
            }.collect {
                //TODO("event 세부 사항 정보 갱신")
            }
        }

    }

    fun setEventName(name: CharSequence) {
        _uiState.update {
            it.copy(eventName = name.toString())
        }
    }

    fun setEventMemo(memo: CharSequence) {
        _uiState.update {
            it.copy(memo = memo.toString())
        }
    }

    fun setEventOpen(isChecked: Boolean) {
        _uiState.update {
            it.copy(isOpen = isChecked)
        }
    }

    fun setEventJoinable(isChecked: Boolean) {
        _uiState.update {
            it.copy(isJoinable = isChecked)
        }
    }

    fun setEventAlarm(index: Int) {
        _uiState.update {
            it.copy(alarm = EventNotification.values()[index])
        }
    }

    fun setEventRepeat(index: Int) {
        _uiState.update {
            it.copy(eventRepeat = EventRepeatTerm.values()[index])
        }
    }

    fun setEventStartDate(time: Long) {
        if(time > uiState.value.endDate.toTimeStampLong()) {
            _event.tryEmit(EventStoryDetailEvent.ShowMessage(R.string.story_detail_message_time_pick_start_time_fail))
            return
        }
        _uiState.update {
            it.copy(startDate = time.toDateStringFormat(), startTime = EventTime(0, 0))
        }
    }

    fun setEventEndDate(time: Long) {
        if(time < uiState.value.startDate.toTimeStampLong()) {
            _event.tryEmit(EventStoryDetailEvent.ShowMessage(R.string.story_detail_message_time_pick_end_time_fail))
            return
        }
        _uiState.update {
            it.copy(endDate = time.toDateStringFormat(), endTime = EventTime(0, 0))
        }
    }

    fun setEventStartTime(hour: Int, min: Int) {
        with(uiState.value) {
            if(startDate == endDate && hour * 60 + min > endTime.hour * 60 + endTime.minute) {
                _event.tryEmit(EventStoryDetailEvent.ShowMessage(R.string.story_detail_message_time_pick_start_time_fail))
                return
            }
        }
        _uiState.update {
            it.copy(startTime = EventTime(hour, min))
        }
    }

    fun setEventEndTime(hour: Int, min: Int) {
        with(uiState.value) {
            if(startDate == endDate && hour * 60 + min < startTime.hour * 60 + startTime.minute) {
                _event.tryEmit(EventStoryDetailEvent.ShowMessage(R.string.story_detail_message_time_pick_end_time_fail))
                return
            }
        }
        _uiState.update {
            it.copy(endTime = EventTime(hour, min))
        }
    }
}