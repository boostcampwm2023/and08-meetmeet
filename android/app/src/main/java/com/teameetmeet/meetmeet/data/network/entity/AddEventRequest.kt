package com.teameetmeet.meetmeet.data.network.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AddEventRequest(
    val title: String,
    val startDate: String,
    val endDate: String,
    val isJoinable: Boolean,
    val isVisible: Boolean,
    val memo: String,
    val repeatTerm: String?,
    val repeatFrequency: Int,
    val repeatEndDate: String
)