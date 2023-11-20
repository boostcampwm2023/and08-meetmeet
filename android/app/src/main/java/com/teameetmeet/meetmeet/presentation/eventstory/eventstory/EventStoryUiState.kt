package com.teameetmeet.meetmeet.presentation.eventstory.eventstory

import com.teameetmeet.meetmeet.data.model.EventStory

data class EventStoryUiState(
    val eventStory: EventStory? = null,
    val isEventMemberUiExpanded: Boolean = false,
    val isLoading: Boolean = false
)