package com.teameetmeet.meetmeet.presentation.eventstory.eventstory.eventmember

import com.teameetmeet.meetmeet.data.model.EventMember

data class EventMemberUiState (
    val eventMember: List<EventMemberState> = emptyList()
)

data class EventMemberState(
    val eventMember: EventMember,
    val isFollowed: Boolean = false
)