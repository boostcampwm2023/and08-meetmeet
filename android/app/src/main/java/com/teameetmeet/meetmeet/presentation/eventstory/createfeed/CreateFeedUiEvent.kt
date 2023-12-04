package com.teameetmeet.meetmeet.presentation.eventstory.createfeed

sealed class CreateFeedUiEvent {
    data class ShowMessage(val messageId: Int, val extraMessage: String = "") : CreateFeedUiEvent()
    data object CreateFeedSuccess : CreateFeedUiEvent()
}