package com.teameetmeet.meetmeet.presentation.eventstory.eventstory

import com.teameetmeet.meetmeet.data.model.EventStory

data class EventStoryUiState(
    val eventStory: EventStory? = null,
    val isEventMemberUiExpanded: Boolean = false,
    val isLoading: Boolean = false,
    val maxMember: Int = SHRINK_MAX_MEMBER
)

const val EXPANDED_MAX_MEMBER = 5
const val SHRINK_MAX_MEMBER = 15