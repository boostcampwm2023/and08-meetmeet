package com.teameetmeet.meetmeet.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FeedDetail(
    @Json(name = "id")
    val id: Int,
    @Json(name = "memo")
    val memo: String?,
    //todo: 이거 모델 이름 바꾸는 게 좋을 듯
    @Json(name = "author")
    val author: Author,
    @Json(name = "contents")
    val contents: List<Content>,
    @Json(name = "comments")
    val comments: List<Comment>
)