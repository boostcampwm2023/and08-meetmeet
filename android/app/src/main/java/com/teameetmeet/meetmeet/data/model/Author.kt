package com.teameetmeet.meetmeet.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

//todo: 서버에 eventMember랑 통일 해주기 요청
@JsonClass(generateAdapter = true)
data class Author(
    @Json(name = "id")
    val id: Int,
    @Json(name = "nickname")
    val nickname: String,
    @Json(name = "profileUrl")
    val profile: String?
)
