package com.teameetmeet.meetmeet.util.date

import java.time.LocalDate
import java.time.ZoneId

fun LocalDate.toStartLong(zoneId: ZoneId = ZoneId.systemDefault()): Long {
    return atStartOfDay(zoneId).toInstant().toEpochMilli()
}

fun LocalDate.toEndLong(zoneId: ZoneId = ZoneId.systemDefault()): Long {
    return plusDays(1).toStartLong(zoneId) - 1
}