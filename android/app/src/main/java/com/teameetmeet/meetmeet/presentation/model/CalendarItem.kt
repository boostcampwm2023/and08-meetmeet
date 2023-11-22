package com.teameetmeet.meetmeet.presentation.model

import com.teameetmeet.meetmeet.data.local.database.entity.Event
import java.time.LocalDate


data class CalendarItem(
    val date: LocalDate? = null,
    //todo: 매핑..
    var events: List<Event> = emptyList(),
    var isSelected: Boolean = false
) {
    fun getDay(): String {
        return date?.dayOfMonth?.toString() ?: ""
    }
}