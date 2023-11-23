package com.teameetmeet.meetmeet.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserProfile(
    @Json(name = "profileUrl")
    val profileImage: String? = null,
    @Json(name = "nickname")
    val nickname: String,
    @Json(name = "email")
    val email: String
)
