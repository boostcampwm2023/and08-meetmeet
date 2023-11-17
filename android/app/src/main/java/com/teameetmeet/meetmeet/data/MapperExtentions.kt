package com.teameetmeet.meetmeet.data

import com.teameetmeet.meetmeet.data.local.database.entity.Event
import com.teameetmeet.meetmeet.data.dto.EventDto
import com.teameetmeet.meetmeet.data.network.entity.EventResponse

fun Event.toEventDto(): EventDto {
    return EventDto(id, title, startDateTime, endDateTime, notification, color)
}

fun EventResponse.toEventDto(): EventDto? {
    return startDate.toDateLong()?.let { startDateLong ->
        endDate.toDateLong()?.let { endDateLong ->
            EventDto(id, title, startDateLong, endDateLong)
        }
    }
}

fun EventDto.toLocalEventEntity(): Event {
    return Event(id, title, startDateTime, endDateTime, notification, color)
}