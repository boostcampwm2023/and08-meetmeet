package com.teameetmeet.meetmeet.presentation.addevent

import androidx.core.util.Pair
import androidx.lifecycle.ViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.teameetmeet.meetmeet.data.model.EventNotification
import com.teameetmeet.meetmeet.data.model.EventRepeatTerm
import com.teameetmeet.meetmeet.data.model.EventTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AddEventViewModel @Inject constructor() : ViewModel() {

    private val _eventName: MutableStateFlow<String> = MutableStateFlow("")

    private val _eventDate: MutableStateFlow<Pair<Long, Long>> = MutableStateFlow(
        Pair(
            MaterialDatePicker.todayInUtcMilliseconds(),
            MaterialDatePicker.todayInUtcMilliseconds()
        )
    )
    val eventDate: StateFlow<Pair<Long, Long>> = _eventDate

    private val _eventStartTime: MutableStateFlow<EventTime> = MutableStateFlow(
        EventTime(0, 0)
    )
    val eventStartTime: StateFlow<EventTime> = _eventStartTime

    private val _eventEndTime: MutableStateFlow<EventTime> = MutableStateFlow(
        EventTime(11, 59)
    )
    val eventEndTime: StateFlow<EventTime> = _eventEndTime

    private val _eventNotification: MutableStateFlow<EventNotification> =
        MutableStateFlow(EventNotification.NONE)
    val eventNotification: StateFlow<EventNotification> = _eventNotification

    private val _eventRepeatTerm: MutableStateFlow<EventRepeatTerm> =
        MutableStateFlow(EventRepeatTerm.NONE)
    val eventRepeatTerm: StateFlow<EventRepeatTerm> = _eventRepeatTerm

    private val _eventMemo: MutableStateFlow<String> = MutableStateFlow("")

    private val _isVisible: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val isVisible: StateFlow<Boolean> = _isVisible

    private val _isJoin: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val isJoin: StateFlow<Boolean> = _isJoin

    fun setEventName(name: String) {
        _eventName.update { name }
    }

    fun setEventDate(start: Long, end: Long) {
        _eventDate.update { Pair(start, end) }
    }

    fun setEventStartTime(hour: Int, minute: Int) {
        _eventStartTime.update { it.copy(hour = hour, minute = minute) }
    }

    fun setEventEndTime(hour: Int, minute: Int) {
        _eventEndTime.update { it.copy(hour = hour, minute = minute) }
    }

    fun setEventNotification(notification: EventNotification) {
        _eventNotification.update { notification }
    }

    fun setEventRepeatTerm(repeatTerm: EventRepeatTerm) {
        _eventRepeatTerm.update { repeatTerm }
    }

    fun setEventMemo(memo: String) {
        _eventMemo.update { memo }
    }

    fun setVisibleState(isChecked: Boolean) {
        _isVisible.update { isChecked }
    }

    fun setJoinState(isChecked: Boolean) {
        _isJoin.update { isChecked }
    }

    fun eventSave() {
        // 일정 추가 저장 api 호출
        println(
            """
            eventName : ${_eventName.value}
            eventDate : ${_eventDate.value}
            eventStartTime : ${_eventStartTime.value}
            eventEndTime : ${_eventEndTime.value}
            eventNoti : ${_eventNotification.value}
            eventRepeatTerm : ${_eventRepeatTerm.value}
            eventMemo : ${_eventMemo.value}
            isVisible : ${_isVisible.value}
            isJoin : ${_isJoin.value}
        """.trimIndent()
        )
    }
}