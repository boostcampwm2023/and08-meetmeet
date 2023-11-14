package com.teameetmeet.meetmeet.presentation.model


data class CalendarItem(
    val day: String = "",
    val events: List<String> = emptyList(),
    val dayOfYear: Int = 0
)