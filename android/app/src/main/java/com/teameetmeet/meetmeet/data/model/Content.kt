package com.teameetmeet.meetmeet.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Content(
    @Json(name = "id")
    val id: Int,
    @Json(name = "mimeType")
    val mimeType: String,
    @Json(name = "path")
    val path: String
)