package com.teameetmeet.meetmeet.data.model

import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class EventStory(
    val id: Int,
    val title: String,
    val startDate: String,
    val endDate: String,
    val eventMembers: List<EventMember>,
    val announcement: String,
    val authority: String,
    val repeatPolicyId: Int?,
    val isJoin: Boolean,
    val isVisible: Boolean,
    val memo: String?,
    val feeds: List<Feed>
) : Serializable
