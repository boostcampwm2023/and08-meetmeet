package com.teameetmeet.meetmeet.presentation.notification.follow

sealed class FollowNotificationUiEvent {
    data class ShowMessage(val message: Int, val extraMessage: String = "") :
        FollowNotificationUiEvent()

    data object NavigateToLoginActivity : FollowNotificationUiEvent()
}
