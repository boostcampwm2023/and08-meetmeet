package com.teameetmeet.meetmeet.presentation.notification.follow

import androidx.annotation.StringRes

sealed class FollowNotificationUiEvent {
    data class ShowMessage(@StringRes val message: Int, val extraMessage: String = "") :
        FollowNotificationUiEvent()

    data object NavigateToLoginActivity : FollowNotificationUiEvent()
}
