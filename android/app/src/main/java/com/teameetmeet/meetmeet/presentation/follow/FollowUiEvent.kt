package com.teameetmeet.meetmeet.presentation.follow

sealed class FollowUiEvent {
    data class ShowMessage(val message: Int, val extraMessage: String = "") : FollowUiEvent()
    data class VisitProfile(val userId: Int, val userNickname: String) : FollowUiEvent()
    data object NavigateToLoginActivity : FollowUiEvent()
}