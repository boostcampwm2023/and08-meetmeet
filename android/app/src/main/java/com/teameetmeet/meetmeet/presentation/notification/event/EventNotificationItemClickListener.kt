package com.teameetmeet.meetmeet.presentation.notification.event

import com.teameetmeet.meetmeet.data.network.entity.EventInvitationNotification

interface EventNotificationItemClickListener {
    fun onInviteClick(event: EventInvitationNotification)

    fun onDeleteClick(event: EventInvitationNotification)
}