package com.teameetmeet.meetmeet.presentation.follow

import androidx.annotation.StringRes

sealed class FollowUiEvent {
    data class ShowMessage(@StringRes val message: Int, val extraMessage: String = "") :
        FollowUiEvent()

    data class VisitProfile(val userId: Int, val userNickname: String) : FollowUiEvent()
    data object NavigateToLoginActivity : FollowUiEvent()
}