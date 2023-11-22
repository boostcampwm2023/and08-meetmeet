package com.teameetmeet.meetmeet.util

import com.teameetmeet.meetmeet.presentation.model.CalendarItem
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun LocalDate.toYearMonth(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월")
    return format(formatter)
}

fun LocalDate.toStartLong(zoneId: ZoneId= ZoneId.systemDefault()): Long {
    return atStartOfDay(zoneId).toInstant().toEpochMilli()
}

fun LocalDate.toEndLong(zoneId: ZoneId= ZoneId.systemDefault()): Long {
    return plusDays(1).toStartLong(zoneId) - 1
}

fun LocalDate.getDayListInMonth(calendarItem: CalendarItem? = null): List<CalendarItem> {
    val dayList = mutableListOf<CalendarItem>()
    val lastDay = YearMonth.from(this).lengthOfMonth()
    val firstDayOfWeek = withDayOfMonth(1).dayOfWeek.value
    repeat(firstDayOfWeek - 1) {
        dayList.add(CalendarItem())
    }
    for (day in 1..lastDay) {
        if (calendarItem != null && calendarItem.date?.dayOfMonth == day) {
            dayList.add(calendarItem)
        } else {
            dayList.add(CalendarItem(date = LocalDate.of(year, month, day)))
        }
    }
    return dayList
}