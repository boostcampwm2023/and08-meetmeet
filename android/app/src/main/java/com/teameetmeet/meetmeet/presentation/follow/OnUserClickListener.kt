package com.teameetmeet.meetmeet.presentation.follow

import com.teameetmeet.meetmeet.data.model.UserProfile

interface OnUserClickListener {

    fun onFollowClick(userProfile: UserProfile)
    fun onUnfollowClick(userProfile: UserProfile)
    fun onInviteEventClick(userProfile: UserProfile, id: Int)
    fun onInviteGroupClick(userProfile: UserProfile, id: Int)
}