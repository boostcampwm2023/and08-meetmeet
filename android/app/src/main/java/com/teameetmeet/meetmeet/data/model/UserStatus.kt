package com.teameetmeet.meetmeet.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserStatus(
    @Json(name = "id")
    val id: Int,
    @Json(name = "nickname")
    val nickname: String,
    @Json(name = "profile")
    val profile: String?,
    @Json(name = "isFollowed")
    val isFollowed: Boolean = true,
    @Json(name = "isJoined")
    val isJoined: Boolean = false,
    val isMe: Boolean = false
)

@JsonClass(generateAdapter = true)
data class UsersResponse(
    @Json(name = "users")
    val users: List<UserStatus>
)