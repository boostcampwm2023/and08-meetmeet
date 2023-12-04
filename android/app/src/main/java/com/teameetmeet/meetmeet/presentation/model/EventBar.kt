package com.teameetmeet.meetmeet.presentation.model

import com.teameetmeet.meetmeet.MeetMeetApp
import com.teameetmeet.meetmeet.R

data class EventBar(
    val id: Int,
    val color: Int = MeetMeetApp.instance.getColor(R.color.grey3),
    val isStart: Boolean = false,
    val isEnd: Boolean = false,
    val hiddenCount: Int = 0
)