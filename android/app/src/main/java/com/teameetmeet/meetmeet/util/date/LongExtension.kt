package com.teameetmeet.meetmeet.util.date

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

fun Long.toLocalDate(zoneId: ZoneId = ZoneId.systemDefault()): LocalDate {
    return Instant.ofEpochMilli(this).atZone(zoneId).toLocalDate()
}

fun Long.toLocalDateTime(zoneId: ZoneId = ZoneId.systemDefault()): LocalDateTime {
    return Instant.ofEpochMilli(this).atZone(zoneId).toLocalDateTime()
}

fun Long.addUtcTimeOffset(): Long {
    return toLocalDateTime().toLong(ZoneId.of("UTC"))
}

fun Long.removeUtcTimeOffset(): Long {
    return toLocalDateTime(ZoneId.of("UTC")).toLong()
}

fun Long.toDateString(format: DateTimeFormat, zoneId: ZoneId = ZoneId.systemDefault()): String {
    return Instant.ofEpochMilli(this).atZone(zoneId).format(format.formatter)
}