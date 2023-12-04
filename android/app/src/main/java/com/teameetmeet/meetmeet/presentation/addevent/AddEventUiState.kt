package com.teameetmeet.meetmeet.presentation.addevent

import com.teameetmeet.meetmeet.presentation.model.EventColor
import com.teameetmeet.meetmeet.presentation.model.EventNotification
import com.teameetmeet.meetmeet.presentation.model.EventRepeatTerm
import com.teameetmeet.meetmeet.presentation.model.EventTime
import com.teameetmeet.meetmeet.util.date.getLocalDateTime
import java.time.LocalDateTime

data class AddEventUiState(
    val eventName: String = "",
    val startDate: LocalDateTime = getLocalDateTime(),
    val endDate: LocalDateTime = getLocalDateTime(),
    val startTime: EventTime = EventTime(0, 0),
    val endTime: EventTime = EventTime(0, 0),
    val alarm: EventNotification = EventNotification.NONE,
    val color: EventColor = EventColor.RED,
    val eventRepeat: EventRepeatTerm = EventRepeatTerm.NONE,
    val eventRepeatFrequency: Int = 1,
    val eventRepeatEndDate: LocalDateTime = getLocalDateTime(),
    val memo: String = "",
    val isOpen: Boolean = false,
    val isJoinable: Boolean = false,
)