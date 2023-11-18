package com.teameetmeet.meetmeet.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Event(
    @PrimaryKey
    val id: Int,
    val title: String,
    val startDateTime: Long,
    val endDateTime: Long,
    val notification: String = "none",
    val color: String = "#FF6565",
)