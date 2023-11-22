package com.teameetmeet.meetmeet.presentation.story.createfeed

import androidx.lifecycle.ViewModel
import com.teameetmeet.meetmeet.presentation.model.MediaItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class CreateFeedViewModel : ViewModel(), MediaItemCancelClickListener {
    private val _feedText = MutableStateFlow<String>("")
    val feedText: StateFlow<String> = _feedText

    private val _mediaList = MutableStateFlow<List<MediaItem>>(listOf())
    val mediaList: StateFlow<List<MediaItem>> = _mediaList

    fun setFeedText(text: CharSequence) {
        _feedText.update { text.toString() }
    }

    fun selectMedia(uris: List<MediaItem>) {
        _mediaList.update { (it + uris).distinct() }
    }

    override fun onClick(mediaItem: MediaItem) {
        _mediaList.update { it.filter { item -> item != mediaItem } }
    }
}