package com.teameetmeet.meetmeet.presentation.model

data class EventBar(
    val id: Int,
    val color: Int,
    val isStart: Boolean = false,
    val isEnd: Boolean = false
)