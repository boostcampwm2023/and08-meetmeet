package com.teameetmeet.meetmeet.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Comment(
    @Json(name = "id")
    val id: Int,
    @Json(name = "memo")
    val memo: String,
    @Json(name = "authorId")
    val authorId: Int
)