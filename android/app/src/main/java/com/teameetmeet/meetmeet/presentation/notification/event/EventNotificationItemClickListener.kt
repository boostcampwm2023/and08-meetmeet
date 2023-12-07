package com.teameetmeet.meetmeet.presentation.notification.event

import com.teameetmeet.meetmeet.data.network.entity.EventInvitationNotification

interface EventNotificationItemClickListener {
    fun onClick(event: EventInvitationNotification)

    fun onDelete(event: EventInvitationNotification)
}