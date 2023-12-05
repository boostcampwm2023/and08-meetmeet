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
    val isFollowed: Boolean = false,
    @Json(name = "isJoined")
    val isJoined: String? = null,
    val isMe: Boolean = false
) {
    companion object {
        const val JOIN_STATUS_JOINABLE = "Joinable"
        const val JOIN_STATUS_PENDING = "Pending"
        const val JOIN_STATUS_ACCEPTED = "Accepted"
    }
}

@JsonClass(generateAdapter = true)
data class UsersResponse(
    @Json(name = "users")
    val users: List<UserStatus>
)