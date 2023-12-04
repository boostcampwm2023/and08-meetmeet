package com.teameetmeet.meetmeet.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EventStory(
    @Json(name = "id")
    val id: Int,
    @Json(name = "title")
    val title: String,
    @Json(name = "startDate")
    val startDate: String,
    @Json(name = "endDate")
    val endDate: String,
    @Json(name = "eventMembers")
    val eventMembers: List<EventMember>,
    @Json(name = "announcement")
    val announcement: String? = "",
    @Json(name = "authority")
    val authority: String?,
    @Json(name = "repeatPolicyId")
    val repeatPolicyId: Int?,
    @Json(name = "isJoinable")
    val isJoin: Boolean,
    @Json(name = "isVisible")
    val isiVisible: Boolean,
    @Json(name = "feeds")
    val feeds: List<Feed>
)
