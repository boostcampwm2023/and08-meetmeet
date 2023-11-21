package com.teameetmeet.meetmeet.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserProfile(
    val profileImage: String?= null,
    val nickname: String
)
