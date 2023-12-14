package com.teameetmeet.meetmeet.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Feed(
    @Json(name = "id")
    val id: Int,
    @Json(name = "thumbnail")
    val thumbnail: String?,
    @Json(name = "memo")
    val memo: String?
)
