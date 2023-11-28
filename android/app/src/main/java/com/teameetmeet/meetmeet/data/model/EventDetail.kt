package com.teameetmeet.meetmeet.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EventDetail(
    @Json(name = "id")
    val id: Int,
    @Json(name = "title")
    val title: String,
    @Json(name = "startDate")
    val startDate: String,
    @Json(name = "endDate")
    val endDate: String,
    @Json(name = "authority")
    val authority: String?,
    @Json(name = "repeatPolicyId")
    val repeatPolicyId: Int?,
    @Json(name = "isJoinable")
    val isJoin: Int,
    @Json(name = "isVisible")
    val isVisible: Boolean = false,
    @Json(name = "repeatTerm")
    val repeatTerm: String?,
    @Json(name = "repeatFrequency")
    val repeatFrequency: Int?
)
