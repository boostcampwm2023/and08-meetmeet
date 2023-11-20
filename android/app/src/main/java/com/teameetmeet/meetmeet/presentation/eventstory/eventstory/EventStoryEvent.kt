package com.teameetmeet.meetmeet.presentation.eventstory.eventstory

sealed class EventStoryEvent {
    data class ShowMessage(val messageId: Int, val extraMessage: String = "") : EventStoryEvent()
}