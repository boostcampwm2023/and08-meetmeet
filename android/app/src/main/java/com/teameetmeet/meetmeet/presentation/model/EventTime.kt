package com.teameetmeet.meetmeet.presentation.model

data class EventTime(val hour: Int, val minute: Int) {
    override fun toString(): String {
        val hourStr = this.hour.toString().padStart(2, '0')
        val minuteStr = this.minute.toString().padStart(2, '0')
        return "${hourStr}:${minuteStr}"
    }
}