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
    @Json(name = "isJoinable")
    val isJoin: Boolean,
    @Json(name = "isVisible")
    val isVisible: Boolean,
    @Json(name = "memo")
    val memo: String,
    @Json(name = "color")
    val color: Int,
    @Json(name = "alarmMinutes")
    val alarmMinutes: Int,
    @Json(name = "repeatTerm")
    val repeatTerm: String?,
    @Json(name = "repeatFrequency")
    val repeatFrequency: Int?,
    @Json(name = "repeatEndDate")
    val repeatEndDate: String?,
)
