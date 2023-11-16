package com.teameetmeet.meetmeet.util

import java.time.LocalDateTime
import java.time.ZoneId

fun LocalDateTime.toLong(zoneId: ZoneId): Long {
    return atZone(zoneId).toInstant().toEpochMilli()
}