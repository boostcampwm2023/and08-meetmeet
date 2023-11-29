package com.teameetmeet.meetmeet.data.network.entity

import com.squareup.moshi.JsonClass
import com.teameetmeet.meetmeet.data.model.EventMember

@JsonClass(generateAdapter = true)
data class EventResponse(
    val id: Int,
    val title: String,
    val startDate: String,
    val endDate: String,
    val eventMembers: List<EventMember>,
    val authority: String,
    val repeatPolicyId: Int?,
    val isJoinable: Boolean
)

@JsonClass(generateAdapter = true)
data class Events(
    val events: List<EventResponse>
)