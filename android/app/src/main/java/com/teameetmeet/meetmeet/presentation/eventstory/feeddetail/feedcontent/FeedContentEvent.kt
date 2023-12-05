package com.teameetmeet.meetmeet.presentation.eventstory.feeddetail.feedcontent

sealed class FeedContentEvent {
    data class ShowMessage(val messageId: Int, val extraMessage: String = "") : FeedContentEvent()
}
