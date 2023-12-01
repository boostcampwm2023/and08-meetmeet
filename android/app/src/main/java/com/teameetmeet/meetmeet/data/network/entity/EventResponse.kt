package com.teameetmeet.meetmeet.data.network.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.teameetmeet.meetmeet.data.model.EventMember

@JsonClass(generateAdapter = true)
data class EventResponse(
    @Json(name = "id")
    val id: Int,
    @Json(name = "title")
    val title: String,
    @Json(name = "startDate")
    val startDate: String,
    @Json(name = "endDate")
    val endDate: String,
    @Json(name = "eventMembers")
    val eventMembers: List<EventMember> = listOf(),
    @Json(name = "authority")
    val authority: String = "",
    @Json(name = "repeatPolicyId")
    val repeatPolicyId: Int? = null,
    @Json(name = "isJoinable")
    val isJoinable: Boolean = false,
    @Json(name = "alarmMinutes")
    val alarmMinutes: Int = -1,
    @Json(name = "color")
    val color: Int = -39579
)

@JsonClass(generateAdapter = true)
data class Events(
    @Json(name = "events")
    val events: List<EventResponse>
)

@JsonClass(generateAdapter = true)
data class SingleEvent(
    @Json(name = "event")
    val event: EventResponse
)