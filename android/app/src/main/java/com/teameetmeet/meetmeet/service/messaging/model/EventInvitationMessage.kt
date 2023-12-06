package com.teameetmeet.meetmeet.service.messaging.model

import com.squareup.moshi.JsonClass
import com.teameetmeet.meetmeet.data.model.EventMember

@JsonClass(generateAdapter = true)
data class EventInvitationMessage(
    val inviteId: Int,
    val eventId: Int,
    val title: String,
    val startDate: String,
    val endDate: String,
    val eventOwner: EventMember,
    val status: String? = null
)