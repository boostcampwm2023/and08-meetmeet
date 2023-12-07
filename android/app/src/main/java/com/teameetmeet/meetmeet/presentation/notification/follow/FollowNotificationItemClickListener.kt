package com.teameetmeet.meetmeet.presentation.notification.follow

import com.teameetmeet.meetmeet.data.network.entity.FollowNotification

interface FollowNotificationItemClickListener {
    fun onDelete(event: FollowNotification)
}