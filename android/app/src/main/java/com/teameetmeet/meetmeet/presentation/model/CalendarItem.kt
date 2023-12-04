package com.teameetmeet.meetmeet.presentation.model

import java.time.LocalDate


data class CalendarItem(
    val date: LocalDate? = null,
    val events: List<EventSimple> = emptyList(),
    val eventBars: List<EventBar?> = emptyList(),
    val isSelected: Boolean = false
) {
    fun getDay(): String {
        return date?.dayOfMonth?.toString() ?: ""
    }
}