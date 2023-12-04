package com.teameetmeet.meetmeet.service.alarm.model

data class EventAlarm(
    val id: Int,
    val triggerTime: Long,
    val alarmMinutes: Int,
    val title: String,
)
