package com.teameetmeet.meetmeet.util

import com.teameetmeet.meetmeet.MeetMeetApp
import com.teameetmeet.meetmeet.R
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import java.util.Locale

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
    return toLocalDateTime(zoneId).format(format.formatter)
}

fun String.toTimeStampLong(format: DateTimeFormat, zoneId: ZoneId = ZoneId.systemDefault()): Long {
    return LocalDateTime.parse(this, format.formatter).toLong(zoneId)
}

fun Long.toDateStringFormat(locale: Locale = Locale.KOREA): String {
    val dateFormat =
        SimpleDateFormat(MeetMeetApp.instance.getString(R.string.common_korea_date_format), locale)
    val date = Date(this)
    return dateFormat.format(date)
}

fun String.toTimeStampLong(locale: Locale = Locale.KOREA): Long {
    val dateFormat =
        SimpleDateFormat(MeetMeetApp.instance.getString(R.string.common_korea_date_format), locale)
    return try {
        val date = dateFormat.parse(this)
        date.time
    } catch (e: Exception) {
        0
    }
}