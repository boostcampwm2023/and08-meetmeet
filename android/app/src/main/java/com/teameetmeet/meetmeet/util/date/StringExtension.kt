package com.teameetmeet.meetmeet.util.date

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeParseException

fun String.toTimeStampLong(format: DateTimeFormat, zoneId: ZoneId = ZoneId.systemDefault()): Long {
    if (this.isEmpty()) return 0
    return try {
        LocalDateTime.parse(this, format.formatter).toLong(zoneId)
    } catch (e: DateTimeParseException) {
        LocalDate.parse(this, format.formatter).toStartLong(zoneId)
    }
}

fun String.toLocalDateTime(format: DateTimeFormat): LocalDateTime? {
    return try {
        LocalDateTime.parse(this, format.formatter)
    } catch (e: DateTimeParseException) {
        null
    }
}