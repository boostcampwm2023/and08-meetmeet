package com.teameetmeet.meetmeet.data.network.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FollowNotificationResponse(
    @Json(name = "type")
    val type: String,
    @Json(name = "body")
    val body: FollowNotification
)

@JsonClass(generateAdapter = true)
data class EventInvitationNotificationResponse(
    @Json(name = "type")
    val type: String,
    @Json(name = "body")
    val body: EventInvitationNotification
)

@JsonClass(generateAdapter = true)
data class FollowNotification(
    @Json(name = "inviteId")
    val inviteId: Int,
    @Json(name = "id")
    val id: Int,
    @Json(name = "nickname")
    val nickname: String,
    @Json(name = "profile")
    val profile: String?,
    @Json(name = "status")
    val status: String
)

@JsonClass(generateAdapter = true)
data class EventInvitationNotification(
    @Json(name = "inviteId")
    val inviteId: Int,
    @Json(name = "eventId")
    val eventId: Int,
    @Json(name = "title")
    val title: String,
    @Json(name = "startDate")
    val startDate: String,
    @Json(name = "endDate")
    val endDate: String,
    @Json(name = "nickname")
    val nickname: String?,
    @Json(name = "profile")
    val profile: String?,
    @Json(name = "status")
    val status: String
)