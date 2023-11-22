package com.teameetmeet.meetmeet.presentation.calendar

import com.teameetmeet.meetmeet.data.local.database.entity.Event

data class DayClickEvent(
    val events: List<Event>
)