package com.teameetmeet.meetmeet.data

import com.teameetmeet.meetmeet.data.local.database.entity.Event
import com.teameetmeet.meetmeet.data.network.entity.EventResponse

fun EventResponse.toEvent(): Event? {
    return startDate.toDateLong()?.let { startDateLong ->
        endDate.toDateLong()?.let { endDateLong ->
            Event(id, title, startDateLong, endDateLong, repeatPolicyId != null).also {
                println(it)
            }
        }
    }
}