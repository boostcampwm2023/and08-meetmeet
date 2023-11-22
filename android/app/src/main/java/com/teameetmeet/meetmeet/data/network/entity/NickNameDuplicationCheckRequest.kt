package com.teameetmeet.meetmeet.data.network.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NickNameDuplicationCheckRequest(
    val nickname: String
)