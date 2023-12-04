package com.teameetmeet.meetmeet.presentation.eventstory.feeddetail

import com.teameetmeet.meetmeet.data.model.FeedDetail

data class FeedDetailUiState(
    val feedId: Int,
    val feedDetail: FeedDetail? = null,
    val typedComment: String = "",
    val contentPage: Int = 0,
    val isLoading: Boolean = false
)