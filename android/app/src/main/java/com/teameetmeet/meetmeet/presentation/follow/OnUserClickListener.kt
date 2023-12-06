package com.teameetmeet.meetmeet.presentation.follow

import com.teameetmeet.meetmeet.data.model.UserStatus

interface OnUserClickListener {
    fun onProfileClick(user: UserStatus)
    fun onFollowClick(user: UserStatus)
    fun onUnfollowClick(user: UserStatus)
    fun onInviteEventClick(user: UserStatus, id: Int)
    fun onInviteGroupClick(user: UserStatus, id: Int)
}