package com.teameetmeet.meetmeet.util

import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

fun LocalDate.toYearMonth(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월")
    return format(formatter)
}

fun LocalDate.getDayListInMonth() : List<String> {
    val dayList = mutableListOf<String>()
    val lastDay = YearMonth.from(this).lengthOfMonth()
    val firstDayOfWeek = withDayOfMonth(1).dayOfWeek.value
    repeat(firstDayOfWeek-1) {
        dayList.add("")
    }
    for(day in 1 .. lastDay) {
        dayList.add("$day")
    }
    return dayList
}