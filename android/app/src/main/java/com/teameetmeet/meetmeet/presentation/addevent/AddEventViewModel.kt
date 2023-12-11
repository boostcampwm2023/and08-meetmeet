package com.teameetmeet.meetmeet.presentation.addevent

import android.widget.RadioGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.data.ExpiredRefreshTokenException
import com.teameetmeet.meetmeet.data.network.entity.EventResponse
import com.teameetmeet.meetmeet.data.repository.CalendarRepository
import com.teameetmeet.meetmeet.presentation.model.EventColor
import com.teameetmeet.meetmeet.presentation.model.EventNotification
import com.teameetmeet.meetmeet.presentation.model.EventRepeatTerm
import com.teameetmeet.meetmeet.presentation.model.EventTime
import com.teameetmeet.meetmeet.service.alarm.AlarmHelper
import com.teameetmeet.meetmeet.service.alarm.model.EventAlarm
import com.teameetmeet.meetmeet.util.date.DateTimeFormat
import com.teameetmeet.meetmeet.util.date.toDateString
import com.teameetmeet.meetmeet.util.date.toLocalDateTime
import com.teameetmeet.meetmeet.util.date.toLong
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class AddEventViewModel @Inject constructor(
    private val calendarRepository: CalendarRepository,
    private val alarmHelper: AlarmHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEventUiState())
    val uiState: StateFlow<AddEventUiState> = _uiState

    private val _event = MutableSharedFlow<AddEventUiEvent>(
        extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val event: SharedFlow<AddEventUiEvent> = _event

    private val _showPlaceholder: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showPlaceholder: StateFlow<Boolean> = _showPlaceholder

    fun eventSave() {
        viewModelScope.launch {
            _showPlaceholder.update { true }
            if (checkEvent()) {
                val startDateTime =
                    _uiState.value.startDate.plusHours(_uiState.value.startTime.hour.toLong())
                        .plusMinutes(_uiState.value.startTime.minute.toLong())
                        .toLong(ZoneId.systemDefault())
                        .toDateString(DateTimeFormat.ISO_DATE_TIME, ZoneId.of("UTC"))
                val endDateTime =
                    _uiState.value.endDate.plusHours(_uiState.value.endTime.hour.toLong())
                        .plusMinutes(_uiState.value.endTime.minute.toLong())
                        .toLong(ZoneId.systemDefault())
                        .toDateString(DateTimeFormat.ISO_DATE_TIME, ZoneId.of("UTC"))

                val repeatEndDate = _uiState.value.eventRepeatEndDate.toLong(ZoneId.systemDefault())
                    .toDateString(DateTimeFormat.ISO_DATE_TIME, ZoneId.of("UTC"))

                with(_uiState.value) {
                    calendarRepository.addEvent(
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
                        when (it) {
                            is ExpiredRefreshTokenException -> {
                                _event.emit(AddEventUiEvent.ShowMessage(R.string.common_message_expired_login))
                                _event.emit(AddEventUiEvent.NavigateToLoginActivity)
                            }

                            is UnknownHostException -> {
                                _event.emit(AddEventUiEvent.ShowMessage(R.string.common_message_no_internet))
                            }

                            else -> {
                                _event.emit(AddEventUiEvent.ShowMessage(R.string.add_event_err_fail))
                            }
                        }
                        _showPlaceholder.update { false }
                    }.collectLatest { events ->
                        events.take(MAX_ALARM_COUNT).forEach { event ->
                            setAlarm(event)
                        }
                        _event.emit(AddEventUiEvent.FinishAddEventActivity)
                        _showPlaceholder.update { false }
                    }
                }
            }
        }
    }

    private fun AddEventUiState.setAlarm(event: EventResponse) {
        val currentTime = LocalDateTime.now().toLong()
        val triggerTime =
            event.startDate.toLocalDateTime(DateTimeFormat.ISO_DATE_TIME)
                ?.minusMinutes(alarm.minutes.toLong())?.toLong(ZoneId.of("UTC"))
        if (triggerTime != null && currentTime <= triggerTime && alarm != EventNotification.NONE) {
            alarmHelper.registerEventAlarm(
                EventAlarm(
                    event.id,
                    triggerTime,
                    alarm.minutes,
                    eventName
                )
            )
        }
    }

    private suspend fun checkEvent(): Boolean {
        val startDateTime =
            _uiState.value.startDate.plusHours(_uiState.value.startTime.hour.toLong())
                .plusMinutes(_uiState.value.startTime.minute.toLong())
        val endDateTime = _uiState.value.endDate.plusHours(_uiState.value.endTime.hour.toLong())
            .plusMinutes(_uiState.value.endTime.minute.toLong())
        if (_uiState.value.eventName.isEmpty()) {
            _event.emit(AddEventUiEvent.ShowMessage(R.string.add_event_err_no_title))
            return false
        } else if (startDateTime.isAfter(endDateTime)) {
            _event.emit(AddEventUiEvent.ShowMessage(R.string.add_event_err_date_time))
            return false
        } else if (_uiState.value.eventRepeat != EventRepeatTerm.NONE && _uiState.value.eventRepeat.days * _uiState.value.eventRepeatFrequency < ChronoUnit.DAYS.between(
                startDateTime, endDateTime
            ) + 1
        ) {
            _event.emit(AddEventUiEvent.ShowMessage(R.string.add_event_err_repeat_term))
            return false
        }
        return true
    }

    fun setEventName(name: CharSequence) {
        _uiState.update {
            it.copy(eventName = name.toString())
        }
    }

    fun setEventDate(startDate: LocalDateTime, endDate: LocalDateTime) {
        if (!endDate.isBefore(_uiState.value.eventRepeatEndDate)) {
            _uiState.update {
                it.copy(
                    startDate = startDate,
                    endDate = endDate,
                    eventRepeatEndDate = endDate.plusYears(1)
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    startDate = startDate,
                    endDate = endDate,
                )
            }
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

    fun setRepeatEndDate(repeatEndDate: LocalDateTime) {
        if (!repeatEndDate.isBefore(_uiState.value.endDate)) {
            _uiState.update {
                it.copy(eventRepeatEndDate = repeatEndDate)
            }
        }
    }

    fun setEventStartTime(hour: Int, min: Int) {
        _uiState.update {
            it.copy(startTime = EventTime(hour, min), endTime = EventTime(hour, min))
        }
    }

    fun setEventEndTime(hour: Int, min: Int) {
        _uiState.update {
            it.copy(endTime = EventTime(hour, min))
        }
    }


    fun setEventColor(radioGroup: RadioGroup, id: Int) {
        val index = radioGroup.indexOfChild(radioGroup.findViewById(id))
        _uiState.update {
            it.copy(color = EventColor.values()[index])
        }
    }

    companion object {
        const val MAX_ALARM_COUNT = 2
    }
}