package com.teameetmeet.meetmeet.presentation.eventstory.createfeed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.data.repository.EventStoryRepository
import com.teameetmeet.meetmeet.presentation.model.FeedMedia
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateFeedViewModel @Inject constructor(
    private val eventStoryRepository: EventStoryRepository
) : ViewModel(), MediaItemCancelClickListener {
    private val _feedText = MutableStateFlow<String>("")
    val feedText: StateFlow<String> = _feedText

    private val _mediaList = MutableStateFlow<List<FeedMedia>>(listOf())
    val mediaList: StateFlow<List<FeedMedia>> = _mediaList

    private val _createFeedUiEvent = MutableSharedFlow<CreateFeedUiEvent>()
    val createFeedUiEvent: SharedFlow<CreateFeedUiEvent> = _createFeedUiEvent

    private val _showPlaceholder = MutableStateFlow<Boolean>(false)
    val showPlaceholder: StateFlow<Boolean> = _showPlaceholder

    fun setFeedText(text: CharSequence) {
        _feedText.update { text.toString() }
    }

    fun selectMedia(uris: List<FeedMedia>) {
        _mediaList.update { (it + uris).distinct() }
    }

    override fun onItemClick(feedMedia: FeedMedia) {
        _mediaList.update { it.filter { item -> item != feedMedia } }
    }

    fun onSave(eventId: Int) {
        viewModelScope.launch {
            if (mediaList.value.isEmpty() && feedText.value.isBlank()) {
                _createFeedUiEvent.emit(
                    CreateFeedUiEvent.ShowMessage(R.string.create_feed_no_contents_message)
                )
            } else if (mediaList.value.size > FeedMedia.MEDIA_AMOUNT_CONSTRAINT) {
                _createFeedUiEvent.emit(
                    CreateFeedUiEvent.ShowMessage(R.string.create_feed_constraint_amount)
                )
            } else if (mediaList.value.sumOf { it.size } > FeedMedia.MEDIA_VOLUME_CONSTRAINT) {
                _createFeedUiEvent.emit(
                    CreateFeedUiEvent.ShowMessage(R.string.create_feed_media_constraint_volume)
                )
            } else {
                mediaList.value
                    .map { it.uri }
                    .let {
                        _showPlaceholder.update { true }
                        eventStoryRepository.createFeed(
                            eventId,
                            feedText.value.ifBlank { null },
                            it.ifEmpty { null }
                        )
                    }
                    .catch {
                        it.printStackTrace()
                        _createFeedUiEvent.emit(CreateFeedUiEvent.ShowMessage(R.string.create_feed_fail_message))
                        _showPlaceholder.update { false }
                    }.collect {
                        _createFeedUiEvent.emit(CreateFeedUiEvent.CreateFeedSuccess)
                        _showPlaceholder.update { false }
                    }
            }
        }
    }
}