package com.teameetmeet.meetmeet.presentation.eventstory.feeddetail

import com.teameetmeet.meetmeet.data.model.Comment

sealed class FeedDetailEvent {
    data class ShowMessage(val messageId: Int, val extraMessage: String = "") : FeedDetailEvent()
    data class DeleteComment(val comment: Comment) : FeedDetailEvent()
    data object FinishFeedDetail : FeedDetailEvent()
}
