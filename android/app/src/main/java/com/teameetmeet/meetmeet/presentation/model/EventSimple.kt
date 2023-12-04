package com.teameetmeet.meetmeet.presentation.model

import com.teameetmeet.meetmeet.data.local.database.entity.Event
import java.io.Serializable

data class EventSimple(
    val id: Int,
    val title: String,
    val startDateTime: Long,
    val endDateTime: Long,
    val color: Int
) : Serializable

fun Event.toEventSimple(): EventSimple {
    return EventSimple(id, title, startDateTime, endDateTime, color)
}