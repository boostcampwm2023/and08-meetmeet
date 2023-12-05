package com.teameetmeet.meetmeet.presentation.eventstory.feeddetail.feedcontent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.data.model.Content
import com.teameetmeet.meetmeet.service.downloading.ImageDownloadHelper
import com.teameetmeet.meetmeet.service.downloading.ImageDownloadWorker
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
class FeedContentViewModel @Inject constructor(
    private val imageDownloadHelper: ImageDownloadHelper
) : ViewModel(), ImageClickListener {

    private val _contents = MutableStateFlow<List<Content>>(emptyList())
    val contents: StateFlow<List<Content>> = _contents

    private val _isTouched = MutableStateFlow<Boolean>(false)
    val isTouched: StateFlow<Boolean> = _isTouched

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _event = MutableSharedFlow<FeedContentEvent>()
    val event: SharedFlow<FeedContentEvent> = _event

    fun fetchContents(content: Array<Content>) {
        _contents.update {
            content.toList()
        }
    }

    private fun changeTouchedStatus() {
        _isTouched.update { it.not() }
    }


    fun resetTouchStatus() {
        _isTouched.update { false }
    }

    fun saveImage(imageIndex: Int, type: String) {
        viewModelScope.launch {
            _isLoading.update { true }
            val content = contents.value.getOrNull(imageIndex)
            if (content == null) {
                _event.emit(
                    FeedContentEvent.ShowMessage(
                        R.string.feed_content_message_image_save_fail
                    )
                )
                _isLoading.update { false }
                return@launch
            }
            imageDownloadHelper.saveImage(content, type).catch {
                _event.emit(
                    FeedContentEvent.ShowMessage(
                        R.string.feed_content_message_image_save_failure
                    )
                )
                _isLoading.update { false }
            }.collect {
                if (it[0].state == WorkInfo.State.SUCCEEDED) {
                    println(it[0].outputData.getString(ImageDownloadWorker.KEY_DOWNLOAD_TYPE))
                    println(it[0])
                    if (it[0].tags.contains(ImageDownloadWorker.TYPE_DOWNLOAD_MANAGER)) {
                        _event.emit(
                            FeedContentEvent.ShowMessage(
                                R.string.feed_content_message_image_save_start
                            )
                        )
                    } else if (it[0].tags.contains(ImageDownloadWorker.TYPE_MEDIA_STORE)) {
                        _event.emit(
                            FeedContentEvent.ShowMessage(
                                R.string.feed_content_message_image_save_success
                            )
                        )
                    }
                    _isLoading.update { false }
                    return@collect
                } else if (it[0].state == WorkInfo.State.FAILED) {
                    _event.emit(
                        FeedContentEvent.ShowMessage(
                            R.string.feed_content_message_image_save_failure
                        )
                    )
                    _isLoading.update { false }
                    return@collect
                }
            }
        }
    }

    override fun onClick() {
        changeTouchedStatus()
    }
}