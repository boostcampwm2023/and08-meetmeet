package com.teameetmeet.meetmeet.presentation.eventstory.feeddetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.data.model.Comment
import com.teameetmeet.meetmeet.data.repository.EventStoryRepository
import com.teameetmeet.meetmeet.presentation.model.EventAuthority
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedDetailViewModel @Inject constructor(
    private val eventStoryRepository: EventStoryRepository
) : ViewModel() {
    private val _feedDetailUiState = MutableStateFlow<FeedDetailUiState>(FeedDetailUiState(0))
    val feedDetailUiState: StateFlow<FeedDetailUiState> = _feedDetailUiState

    private val _feedDetailEvent = MutableSharedFlow<FeedDetailEvent>()
    val feedDetailEvent: SharedFlow<FeedDetailEvent> = _feedDetailEvent

    fun setFeedId(feedId: Int) {
        _feedDetailUiState.update { it.copy(feedId = feedId) }
    }

    fun setContentPage(page: Int) {
        _feedDetailUiState.update { it.copy(contentPage = page) }
    }

    fun setFeedAuthority(authority: EventAuthority) {
        _feedDetailUiState.update { it.copy(authority = authority) }
    }

    fun onCommentTextChanged(charSequence: CharSequence) {
        _feedDetailUiState.update { it.copy(typedComment = charSequence.toString()) }
    }

    fun getFeedDetail() {
        viewModelScope.launch {
            eventStoryRepository.getFeedDetail(_feedDetailUiState.value.feedId)
                .onStart {
                    _feedDetailUiState.update { it.copy(isLoading = true) }
                }.catch {
                    _feedDetailUiState.update { feedDetail ->
                        feedDetail.copy(isLoading = false)
                    }
                    //todo: 예외처리
                    throw it
                }.collectLatest { response ->
                    _feedDetailUiState.update {
                        it.copy(
                            feedId = response.id,
                            feedDetail = response,
                            isLoading = false,
                            typedComment = ""
                        )
                    }
                }
        }
    }

    fun addComment() {
        if (_feedDetailUiState.value.typedComment.isBlank()) return
        viewModelScope.launch {
            eventStoryRepository.addFeedComment(
                _feedDetailUiState.value.feedId,
                _feedDetailUiState.value.typedComment
            ).catch {
                //todo: 예외처리
                throw it
            }.collectLatest {
                getFeedDetail()
            }
        }
    }

    fun deleteFeed() {
        viewModelScope.launch {
            eventStoryRepository.deleteFeed(feedDetailUiState.value.feedId)
                .catch {
                    _feedDetailEvent.emit(
                        FeedDetailEvent.ShowMessage(R.string.feed_detail_delete_fail_message)
                    )
                }.collectLatest {
                    _feedDetailEvent.emit(FeedDetailEvent.FinishFeedDetail)
                }
        }
    }

    fun deleteComment(comment: Comment) {
        viewModelScope.launch {
            eventStoryRepository.deleteFeedComment(feedDetailUiState.value.feedId, comment.id)
                .catch {
                    _feedDetailEvent.emit(
                        FeedDetailEvent.ShowMessage(R.string.feed_detail_delete_comment_fail_message)
                    )
                }.collectLatest {
                    getFeedDetail()
                }
        }
    }
}