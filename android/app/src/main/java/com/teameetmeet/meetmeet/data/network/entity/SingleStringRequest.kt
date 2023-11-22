package com.teameetmeet.meetmeet.data.network.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class SingleStringRequest (
    val stringValue: String
)