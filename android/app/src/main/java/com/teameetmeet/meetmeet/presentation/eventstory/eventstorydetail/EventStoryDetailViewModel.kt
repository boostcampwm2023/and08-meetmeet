package com.teameetmeet.meetmeet.presentation.eventstory.eventstorydetail

import android.util.Log
import android.widget.RadioGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.data.ExpiredRefreshTokenException
import com.teameetmeet.meetmeet.data.repository.EventStoryRepository
import com.teameetmeet.meetmeet.presentation.model.EventAuthority
import com.teameetmeet.meetmeet.presentation.model.EventColor
import com.teameetmeet.meetmeet.presentation.model.EventNotification
import com.teameetmeet.meetmeet.presentation.model.EventRepeatTerm
import com.teameetmeet.meetmeet.presentation.model.EventTime
import com.teameetmeet.meetmeet.util.date.DateTimeFormat
import com.teameetmeet.meetmeet.util.date.toDateString
import com.teameetmeet.meetmeet.util.date.toLocalDateTime
import com.teameetmeet.meetmeet.util.date.toLong
import com.teameetmeet.meetmeet.util.date.toTimeStampLong
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import java.time.ZoneId
import javax.inject.Inject


@HiltViewModel
class EventStoryDetailViewModel @Inject constructor(
    private val eventStoryRepository: EventStoryRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<EventStoryDetailUiState>(EventStoryDetailUiState(authority = EventAuthority.OWNER))
    val uiState: StateFlow<EventStoryDetailUiState> = _uiState

    private val _event = MutableSharedFlow<EventStoryDetailEvent>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val event: SharedFlow<EventStoryDetailEvent> = _event

    fun fetchEventId(storyId: Int) {
        _uiState.update {
            it.copy(eventId = storyId)
        }
    }

    fun fetchStoryDetail() {
        viewModelScope.launch {
            eventStoryRepository.getEventStoryDetail(uiState.value.eventId).catch {
                when(it) {
                    is ExpiredRefreshTokenException -> {
                        _event.tryEmit(EventStoryDetailEvent.NavigateToLoginActivity)
                    }
                    is UnknownHostException -> {
                        _event.tryEmit(
                            EventStoryDetailEvent.ShowMessage(
                                R.string.common_message_no_internet
                            )
                        )
                    }
                    else -> {
                        _event.tryEmit(
                            EventStoryDetailEvent.ShowMessage(
                                R.string.story_detail_message_story_detail_fetch_fail,
                                it.message.orEmpty()
                            )
                        )
                    }
                }

            }.collect {eventDetail ->
                _uiState.update {
                    with(eventDetail) {
                        val startLocalDateTime = startDate.toTimeStampLong(DateTimeFormat.ISO_DATE_TIME, ZoneId.of("UTC")).toLocalDateTime()
                        val endLocalDateTime = endDate.toTimeStampLong(DateTimeFormat.ISO_DATE_TIME, ZoneId.of("UTC")).toLocalDateTime()
                        it.copy(
                            eventId = id,
                            eventName = title,
                            startDate = startLocalDateTime.toLong().toDateString(DateTimeFormat.LOCAL_DATE),
                            endDate = endLocalDateTime.toLong().toDateString(DateTimeFormat.LOCAL_DATE),
                            startTime = EventTime(startLocalDateTime.hour, startLocalDateTime.minute),
                            endTime = EventTime(endLocalDateTime.hour, endLocalDateTime.minute),
                            eventRepeatFrequency = repeatFrequency?:0,
                            eventRepeat = when(repeatTerm) {
                                "DAY" -> EventRepeatTerm.DAY
                                "WEEK" -> EventRepeatTerm.WEEK
                                "MONTH" -> EventRepeatTerm.MONTH
                                "YEAR" -> EventRepeatTerm.YEAR
                                else -> EventRepeatTerm.NONE
                            },
                            isJoinable = isJoin,
                            isOpen = isVisible==1,
                            authority = when (authority) {
                                "OWNER" -> EventAuthority.OWNER
                                "MEMBER" -> EventAuthority.PARTICIPANT
                                else -> EventAuthority.GUEST
                            },
                            isRepeatEvent = repeatTerm!=null
                        )

                    }
                }
            }
        }
    }

    fun deleteEvent(isAll: Boolean = false) {
        viewModelScope.launch {
            eventStoryRepository.deleteEventStory(uiState.value.eventId, isAll).catch {
                when(it) {
                    is ExpiredRefreshTokenException -> {
                        _event.tryEmit(EventStoryDetailEvent.NavigateToLoginActivity)
                    }

                    is UnknownHostException -> {
                        _event.tryEmit(
                            EventStoryDetailEvent.ShowMessage(
                                R.string.common_message_no_internet
                            )
                        )
                    }
                    else -> {
                        _event.tryEmit(
                            EventStoryDetailEvent.ShowMessage(
                                R.string.story_detail_message_event_story_delete_fail,
                                it.message.orEmpty()
                            )
                        )
                    }
                }
            }.collect {
                _event.tryEmit(EventStoryDetailEvent.FinishEventStoryActivity)
            }
        }
    }

    fun editEvent(isAll: Boolean = false) {
        viewModelScope.launch {
            val startDateTime =
                _uiState.value.startDate.toTimeStampLong(DateTimeFormat.LOCAL_DATE).toLocalDateTime()
                    .plusHours(_uiState.value.startTime.hour.toLong())
                    .plusMinutes(_uiState.value.startTime.minute.toLong())
                    .toLong(ZoneId.systemDefault())
                    .toDateString(DateTimeFormat.GLOBAL_DATE_TIME, ZoneId.of("UTC"))
            val endDateTime =
                _uiState.value.endDate.toTimeStampLong(DateTimeFormat.LOCAL_DATE).toLocalDateTime()
                    .plusHours(_uiState.value.endTime.hour.toLong())
                    .plusMinutes(_uiState.value.endTime.minute.toLong())
                    .toLong(ZoneId.systemDefault())
                    .toDateString(DateTimeFormat.GLOBAL_DATE_TIME, ZoneId.of("UTC"))

            val repeatEndDate = _uiState.value.eventRepeatEndDate.toTimeStampLong(DateTimeFormat.LOCAL_DATE)
                .toDateString(DateTimeFormat.GLOBAL_DATE_TIME, ZoneId.of("UTC"))

            with(_uiState.value) {
                eventStoryRepository.editEventStory(
                    eventId = eventId,
                    isAll = isAll,
                    title = eventName,
                    startDate = startDateTime,
                    endDate = endDateTime,
                    isJoinable = isJoinable,
                    isVisible = isOpen,
                    memo = memo,
                    repeatTerm = eventRepeat.value,
                    repeatFrequency = eventRepeatFrequency,
                    repeatEndDate = repeatEndDate,
                    color = color,
                    alarm = alarm,
                ).catch {
                    _event.emit(EventStoryDetailEvent.ShowMessage(R.string.story_detail_message_edit_message_fail, extraMessage = it.message.orEmpty()))
                }.collect {
                    _event.emit(EventStoryDetailEvent.ShowMessage(R.string.story_detail_success_edit_event))
                    _event.emit(EventStoryDetailEvent.FinishEventStoryDetail)
                }
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

    fun setEventRepeatFrequency(frequency: String) {
        _uiState.update {
            it.copy(eventRepeatFrequency = frequency.toInt())
        }
    }

    fun setEventStartDate(time: Long) {
        if (time > uiState.value.endDate.toTimeStampLong(DateTimeFormat.LOCAL_DATE)) {
            _event.tryEmit(EventStoryDetailEvent.ShowMessage(R.string.story_detail_message_time_pick_start_time_fail))
            return
        }
        _uiState.update {
            it.copy(
                startDate = time.toDateString(DateTimeFormat.LOCAL_DATE),
                startTime = EventTime(0, 0)
            )
        }
    }

    fun setEventEndDate(time: Long) {
        if (time < uiState.value.startDate.toTimeStampLong(DateTimeFormat.LOCAL_DATE)) {
            _event.tryEmit(EventStoryDetailEvent.ShowMessage(R.string.story_detail_message_time_pick_end_time_fail))
            return
        }
        if(time > uiState.value.eventRepeatEndDate.toTimeStampLong(DateTimeFormat.LOCAL_DATE)) {
            _event.tryEmit(EventStoryDetailEvent.ShowMessage((R.string.story_detail_message_time_pick_end_time_fail_after_repeat_end)))
            return
        }
        _uiState.update {
            it.copy(
                endDate = time.toDateString(DateTimeFormat.LOCAL_DATE),
                endTime = EventTime(0, 0)
            )
        }
    }

    fun setRepeatEndDate(time: Long) {
        if (time < uiState.value.endDate.toTimeStampLong(DateTimeFormat.LOCAL_DATE)) {
            _event.tryEmit(EventStoryDetailEvent.ShowMessage(R.string.story_detail_message_repeat_end_date_fail))
            return
        }
        _uiState.update {
            it.copy(eventRepeatEndDate = time.toDateString(DateTimeFormat.LOCAL_DATE))
        }
    }

    fun setEventStartTime(hour: Int, min: Int) {
        with(uiState.value) {
            if (startDate == endDate && hour * 60 + min > endTime.hour * 60 + endTime.minute) {
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
            if (startDate == endDate && hour * 60 + min < startTime.hour * 60 + startTime.minute) {
                _event.tryEmit(EventStoryDetailEvent.ShowMessage(R.string.story_detail_message_time_pick_end_time_fail))
                return
            }
        }
        _uiState.update {
            it.copy(endTime = EventTime(hour, min))
        }
    }


    fun setEventColor(radioGroup: RadioGroup, id: Int) {
        val index = radioGroup.indexOfChild(radioGroup.findViewById(id))
        _uiState.update {
            it.copy(color = EventColor.values()[index])
        }
        Log.d("test", uiState.value.toString())
    }
}