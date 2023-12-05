package com.teameetmeet.meetmeet.data.network.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)

data class EventInviteAcceptRequest(
    @Json(name = "inviteId")
    val inviteId: Int,
    @Json(name = "eventId")
    val eventId: Int
)