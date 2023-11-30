package com.teameetmeet.meetmeet.presentation.eventstory.eventstory.eventmember

import com.teameetmeet.meetmeet.data.model.UserWithFollowStatus

interface EventMemberClickListener {

    fun onClick(userWithFollowStatus: UserWithFollowStatus)
}