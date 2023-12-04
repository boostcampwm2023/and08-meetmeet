package com.teameetmeet.meetmeet.data.network.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RefreshAccessTokenRequest(
    @Json(name = "refreshToken")
    val refreshToken: String
)
