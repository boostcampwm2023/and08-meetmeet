package com.teameetmeet.meetmeet.presentation.model

import java.time.LocalDate


data class CalendarItem(
    val date: LocalDate? = null,
    val events: List<String> = emptyList(),
) {
    fun getDay() : String {
        return date?.dayOfMonth?.toString() ?: ""
    }
}