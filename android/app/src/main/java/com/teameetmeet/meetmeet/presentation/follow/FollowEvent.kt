package com.teameetmeet.meetmeet.presentation.follow

sealed class FollowEvent {
    data class ShowMessage(val message: Int, val extraMessage: String = "") : FollowEvent()
    data class VisitProfile(val userId: Int, val userNickname: String) : FollowEvent()
    data object NavigateToLoginActivity : FollowEvent()
}