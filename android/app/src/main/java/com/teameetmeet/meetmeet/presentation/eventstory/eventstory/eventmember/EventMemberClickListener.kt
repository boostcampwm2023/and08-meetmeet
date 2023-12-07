package com.teameetmeet.meetmeet.presentation.eventstory.eventstory.eventmember

import com.teameetmeet.meetmeet.data.model.UserStatus

interface EventMemberClickListener {

    fun onButtonClick(userStatus: UserStatus)

    fun onItemViewClick(userStatus: UserStatus)
}