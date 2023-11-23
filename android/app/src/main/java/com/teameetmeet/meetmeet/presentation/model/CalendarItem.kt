package com.teameetmeet.meetmeet.presentation.model

import java.time.LocalDate


data class CalendarItem(
    val date: LocalDate? = null,
    var events: List<EventSimple> = emptyList(),
    var isSelected: Boolean = false
) {
    fun getDay(): String {
        return date?.dayOfMonth?.toString() ?: ""
    }
}