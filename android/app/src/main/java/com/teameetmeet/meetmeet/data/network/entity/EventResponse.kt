package com.teameetmeet.meetmeet.data.network.entity

import com.squareup.moshi.JsonClass
import com.teameetmeet.meetmeet.data.model.EventMember

@JsonClass(generateAdapter = true)
data class EventResponse(
    val id: Int,
    val title: String,
    val startDate: String,
    val endDate: String,
    val eventMembers: List<EventMember> = listOf(),
    val authority: String = "",
    val repeatPolicyId: Int? = null,
    val isJoinable: Boolean = false,
    val alarmMinutes: Int = -1,
    val color: Int = -39579
)

@JsonClass(generateAdapter = true)
data class Events(
    val events: List<EventResponse>
)