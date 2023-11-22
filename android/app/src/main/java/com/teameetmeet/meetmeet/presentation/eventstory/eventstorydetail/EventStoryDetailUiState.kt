package com.teameetmeet.meetmeet.presentation.eventstory.eventstorydetail

import com.teameetmeet.meetmeet.presentation.model.EventAuthority
import com.teameetmeet.meetmeet.presentation.model.EventColor
import com.teameetmeet.meetmeet.presentation.model.EventNotification
import com.teameetmeet.meetmeet.presentation.model.EventRepeatTerm

data class EventStoryDetailUiState(
    val eventName: String ="",
    val startTime: Long = 0,
    val endTime: Long = 0,
    val alarm: EventNotification = EventNotification.NONE,
    val color: EventColor = EventColor.RED,
    val eventRepeat: EventRepeatTerm = EventRepeatTerm.NONE,
    val memo: String = "",
    val isOpen: Boolean = false,
    val isJoinable: Boolean = false,
    val authority: EventAuthority = EventAuthority.GUEST
)
