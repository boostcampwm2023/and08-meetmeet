package com.teameetmeet.meetmeet.util.date

import java.time.LocalDateTime
import java.time.ZoneId

fun LocalDateTime.toLong(zoneId: ZoneId= ZoneId.systemDefault()): Long {
    return atZone(zoneId).toInstant().toEpochMilli()
}