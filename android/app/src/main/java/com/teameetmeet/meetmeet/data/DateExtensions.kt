package com.teameetmeet.meetmeet.data

import com.teameetmeet.meetmeet.util.toLocalDateTime
import com.teameetmeet.meetmeet.util.toLong
import java.text.ParseException
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val dateTimeFormatterISO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
private val dateTimeFormatterKor = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분")

fun String.toDateLong(): Long? {
    return try {
        LocalDateTime.parse(this, dateTimeFormatterISO).toLong(ZoneId.systemDefault())
    } catch (e: ParseException) {
        null
    }
}

fun Long.toDateString(): String {
    return toLocalDateTime(ZoneId.systemDefault()).format(dateTimeFormatterKor)
}