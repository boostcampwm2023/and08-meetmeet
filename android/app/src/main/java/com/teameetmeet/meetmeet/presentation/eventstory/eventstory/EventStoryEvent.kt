package com.teameetmeet.meetmeet.presentation.eventstory.eventstory

sealed class EventStoryEvent {
    data object ShowProgressBar : EventStoryEvent()
    data class ShowMessage(val messageId: Int, val extraMessage: String = "") : EventStoryEvent()

    data object StopShowProgressBar : EventStoryEvent()
}