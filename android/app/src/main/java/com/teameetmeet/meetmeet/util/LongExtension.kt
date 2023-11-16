package com.teameetmeet.meetmeet.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

fun Long.toLocalDate(zoneId: ZoneId): LocalDate {
    return Instant.ofEpochMilli(this).atZone(zoneId).toLocalDate()
}

fun Long.toLocalDateTime(zoneId: ZoneId): LocalDateTime {
    return Instant.ofEpochMilli(this).atZone(zoneId).toLocalDateTime()
}

fun Long.addUtcTimeOffset(): Long {
    return toLocalDateTime(ZoneId.systemDefault()).toLong(ZoneId.of("UTC"))
}

fun Long.removeUtcTimeOffset(): Long {
    return toLocalDateTime(ZoneId.of("UTC")).toLong(ZoneId.systemDefault())
}
