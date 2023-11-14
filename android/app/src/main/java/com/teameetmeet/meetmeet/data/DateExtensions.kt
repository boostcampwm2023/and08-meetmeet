package com.teameetmeet.meetmeet.data

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA)

fun String.toDateLong(): Long? {
    return try {
        simpleDateFormat.parse(this)?.time
    } catch (e: ParseException) {
        null
    }
}

fun Long.toDateString(): String {
    return simpleDateFormat.format(Date(this))
}