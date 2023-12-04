package com.teameetmeet.meetmeet.util.date

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

fun getLocalDate(): LocalDate {
    return LocalDate.now(ZoneId.systemDefault()).atStartOfDay().toLocalDate()
}

fun getLocalDateTime(): LocalDateTime {
    return LocalDateTime.now(ZoneId.systemDefault())
}