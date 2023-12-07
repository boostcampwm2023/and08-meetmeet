package com.teameetmeet.meetmeet.presentation.eventstory.eventstory.eventmember

sealed class EventMemberEvent {

    data class ShowMessage(val messageId: Int, val extraMessage: String = "") : EventMemberEvent()

    data class NavigateToVisitCalendarActivity(val userId: Int, val userNickname: String) :
        EventMemberEvent()
}
