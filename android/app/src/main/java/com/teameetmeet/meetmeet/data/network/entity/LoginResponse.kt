package com.teameetmeet.meetmeet.data.network.entity

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String
)
