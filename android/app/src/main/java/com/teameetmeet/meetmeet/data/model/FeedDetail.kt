package com.teameetmeet.meetmeet.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FeedDetail(
    @Json(name = "id")
    val id: Int,
    @Json(name = "memo")
    val memo: String?,
    @Json(name = "author")
    val author: Author,
    @Json(name = "contents")
    val contents: List<Content>,
    @Json(name = "comments")
    val comments: List<Comment>
)