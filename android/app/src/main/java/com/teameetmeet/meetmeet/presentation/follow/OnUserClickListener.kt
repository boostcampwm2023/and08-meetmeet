package com.teameetmeet.meetmeet.presentation.follow

import com.teameetmeet.meetmeet.data.model.UserWithFollowStatus

interface OnUserClickListener {

    fun onFollowClick(user: UserWithFollowStatus)
    fun onUnfollowClick(user: UserWithFollowStatus)
    fun onInviteEventClick(user: UserWithFollowStatus, id: Int)
    fun onInviteGroupClick(user: UserWithFollowStatus, id: Int)
}