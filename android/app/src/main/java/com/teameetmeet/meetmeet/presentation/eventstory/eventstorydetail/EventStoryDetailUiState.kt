package com.teameetmeet.meetmeet.presentation.eventstory.eventstorydetail

import com.teameetmeet.meetmeet.presentation.model.EventAuthority
import com.teameetmeet.meetmeet.presentation.model.EventColor
import com.teameetmeet.meetmeet.presentation.model.EventNotification
import com.teameetmeet.meetmeet.presentation.model.EventRepeatTerm
import com.teameetmeet.meetmeet.presentation.model.EventTime

data class EventStoryDetailUiState(
    val eventId: Int = 0,
    val eventName: String ="",
    val startDate: String = "",
    val endDate: String = "",
    val startTime: EventTime = EventTime(0 , 0),
    val endTime: EventTime = EventTime(0, 0),
    val alarm: EventNotification = EventNotification.NONE,
    val color: EventColor = EventColor.RED,
    val eventRepeat: EventRepeatTerm = EventRepeatTerm.NONE,
    val eventRepeatFrequency: Int = 0,
    val eventRepeatEndDate: String = "2040년 01월 01일",
    val memo: String = "",
    val isOpen: Boolean = false,
    val isJoinable: Boolean = false,
    val authority: EventAuthority = EventAuthority.GUEST,
    val isRepeatEvent: Boolean = false
)
