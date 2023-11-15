package com.teameetmeet.meetmeet.data.dto

data class EventDto(
    val id: Int,
    val title: String,
    val startDateTime: Long,
    val endDateTime: Long,
    val notification: String = "none",
    val color: String = "#FF6565",
)