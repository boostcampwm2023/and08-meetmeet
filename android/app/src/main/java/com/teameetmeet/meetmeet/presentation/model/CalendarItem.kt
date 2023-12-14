package com.teameetmeet.meetmeet.presentation.model

import java.time.LocalDate
import java.time.YearMonth


data class CalendarItem(
    val date: LocalDate? = null,
    val events: List<EventSimple> = emptyList(),
    val eventBars: List<EventBar?> = emptyList(),
    val isSelected: Boolean = false
) {
    fun getDay(): String {
        return date?.dayOfMonth?.toString() ?: ""
    }

    companion object {
        fun getListFrom(date: LocalDate): List<CalendarItem> {
            val dayList = mutableListOf<CalendarItem>()
            val lastDay = YearMonth.from(date).lengthOfMonth()
            val firstDayOfWeek = date.withDayOfMonth(1).dayOfWeek.value
            repeat(firstDayOfWeek - 1) {
                dayList.add(CalendarItem())
            }
            (1..lastDay).forEach { day ->
                if (date.dayOfMonth == day) {
                    dayList.add(CalendarItem(date = date, isSelected = true))
                } else {
                    dayList.add(CalendarItem(date = LocalDate.of(date.year, date.month, day)))
                }
            }
            return dayList
        }
    }
}