package com.teameetmeet.meetmeet.presentation.eventstory.eventstory.eventmember

import com.teameetmeet.meetmeet.data.model.UserStatus

interface EventMemberClickListener {

    fun onClick(userStatus: UserStatus)
}