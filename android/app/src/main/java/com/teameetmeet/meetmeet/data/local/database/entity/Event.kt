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
    val isRepeat: Boolean = false,
    val notification: Int = -1,
    val color: Int = -39579
) {
    fun getTriggerTime(): Long {
        return startDateTime - notification * 60 * 1000
    }
}