package com.teameetmeet.meetmeet.data.network.entity

import com.squareup.moshi.Json
import com.teameetmeet.meetmeet.data.model.EventDetail

data class EventStoryDetailResponse(
    @Json(name = "result")
    val result: EventDetail
)
