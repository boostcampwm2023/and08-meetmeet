package com.teameetmeet.meetmeet.presentation.eventstory.eventstory.eventmember

import androidx.annotation.StringRes

sealed class EventMemberEvent {

    data class ShowMessage(@StringRes val messageId: Int, val extraMessage: String = "") : EventMemberEvent()

    data class NavigateToVisitCalendarActivity(val userId: Int, val userNickname: String) :
        EventMemberEvent()

    data object NavigateToLoginActivity: EventMemberEvent()
}
