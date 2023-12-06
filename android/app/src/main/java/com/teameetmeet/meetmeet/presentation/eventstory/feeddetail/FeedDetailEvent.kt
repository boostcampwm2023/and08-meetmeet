package com.teameetmeet.meetmeet.presentation.eventstory.feeddetail

sealed class FeedDetailEvent {
    data class ShowMessage(val messageId: Int, val extraMessage: String = "") : FeedDetailEvent()
    data object FinishFeedDetail : FeedDetailEvent()
}
