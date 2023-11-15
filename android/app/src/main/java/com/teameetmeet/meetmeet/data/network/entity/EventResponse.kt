package com.teameetmeet.meetmeet.data.network.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EventResponse(
    val id: Int,
    val title: String,
    val startDate: String,
    val endDate: String,
    val eventMembers: List<UserResponse>,
    val authorityId: Int,
    val repeatPolicyId: Int,
    val isJoin: Boolean
)