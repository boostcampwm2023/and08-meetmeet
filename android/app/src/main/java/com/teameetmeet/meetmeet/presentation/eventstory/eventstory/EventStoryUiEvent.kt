package com.teameetmeet.meetmeet.presentation.eventstory.eventstory

sealed class EventStoryUiEvent {
    data class ShowMessage(val messageId: Int, val extraMessage: String = "") : EventStoryUiEvent()

    data object NavigateToLoginActivity : EventStoryUiEvent()

}