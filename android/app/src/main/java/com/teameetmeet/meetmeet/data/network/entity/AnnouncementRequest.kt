package com.teameetmeet.meetmeet.data.network.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AnnouncementRequest(
    @Json(name = "announcement")
    val announcement: String?
)