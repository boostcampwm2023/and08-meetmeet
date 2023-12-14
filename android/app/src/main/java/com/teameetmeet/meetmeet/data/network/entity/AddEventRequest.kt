package com.teameetmeet.meetmeet.data.network.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AddEventRequest(
    @Json(name = "title")
    val title: String,
    @Json(name = "startDate")
    val startDate: String,
    @Json(name = "endDate")
    val endDate: String,
    @Json(name = "isJoinable")
    val isJoinable: Boolean,
    @Json(name = "isVisible")
    val isVisible: Boolean,
    @Json(name = "alarmMinutes")
    val alarmMinutes: Int,
    @Json(name = "memo")
    val memo: String?,
    @Json(name = "color")
    val color: Int,
    @Json(name = "repeatTerm")
    val repeatTerm: String?,
    @Json(name = "repeatFrequency")
    val repeatFrequency: Int?,
    @Json(name = "repeatEndDate")
    val repeatEndDate: String?
)