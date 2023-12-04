package com.teameetmeet.meetmeet.presentation.follow

sealed class FollowEvent {
    data class ShowMessage(val message: Int, val extraMessage: String = "") : FollowEvent()
}