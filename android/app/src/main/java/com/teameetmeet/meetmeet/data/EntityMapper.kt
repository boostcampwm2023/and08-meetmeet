package com.teameetmeet.meetmeet.data

import com.teameetmeet.meetmeet.data.local.database.entity.Event
import com.teameetmeet.meetmeet.data.network.entity.EventResponse
import com.teameetmeet.meetmeet.data.network.entity.UserEventResponse
import com.teameetmeet.meetmeet.util.date.DateTimeFormat
import com.teameetmeet.meetmeet.util.date.toTimeStampLong
import java.time.ZoneId

fun EventResponse.toEvent(): Event {
    val startDateLong = startDate.toTimeStampLong(DateTimeFormat.ISO_DATE_TIME, ZoneId.of("UTC"))
    val endDateLong = endDate.toTimeStampLong(DateTimeFormat.ISO_DATE_TIME, ZoneId.of("UTC"))
    return Event(id, title, startDateLong, endDateLong, repeatPolicyId != null, alarmMinutes, color)
}

fun UserEventResponse.toEvent(): Event {
    val startDateLong = startDate.toTimeStampLong(DateTimeFormat.ISO_DATE_TIME, ZoneId.of("UTC"))
    val endDateLong = endDate.toTimeStampLong(DateTimeFormat.ISO_DATE_TIME, ZoneId.of("UTC"))

    return Event(
        id = id,
        title = title,
        startDateTime = startDateLong,
        endDateTime = endDateLong,
        color = color
    )
}