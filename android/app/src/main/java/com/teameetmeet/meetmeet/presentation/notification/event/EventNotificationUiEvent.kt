package com.teameetmeet.meetmeet.presentation.notification.event

import com.teameetmeet.meetmeet.data.network.entity.EventInvitationNotification

sealed class EventNotificationUiEvent {
    data class ShowAcceptDialog(val event: EventInvitationNotification) : EventNotificationUiEvent()
    data class ShowMessage(val message: Int, val extraMessage: String = "") :
        EventNotificationUiEvent()

    data object NavigateToLoginActivity : EventNotificationUiEvent()
}
