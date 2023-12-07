package com.teameetmeet.meetmeet.presentation.eventstory.feeddetail.feedcontent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.data.model.Content
import com.teameetmeet.meetmeet.service.downloading.ImageDownloadHelper
import com.teameetmeet.meetmeet.service.downloading.ImageDownloadWorker
import com.teameetmeet.meetmeet.service.downloading.ImageDownloadWorker.Companion.TYPE_IMAGE
import com.teameetmeet.meetmeet.service.downloading.ImageDownloadWorker.Companion.TYPE_VIDEO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedContentViewModel @Inject constructor(
    private val imageDownloadHelper: ImageDownloadHelper
) : ViewModel(), ImageClickListener {

    private val _contents = MutableStateFlow<List<Content>>(emptyList())
    val contents: StateFlow<List<Content>> = _contents

    private val _currentPage = MutableStateFlow<Int>(0)
    val currentPage: StateFlow<Int> = _currentPage

    private val _isTouched = MutableStateFlow<Boolean>(false)
    val isTouched: StateFlow<Boolean> = _isTouched

    private val _loadingStatus = MutableStateFlow<List<Boolean>>(emptyList())
    private val loadingStatus: StateFlow<List<Boolean>> = _loadingStatus

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _event = MutableSharedFlow<FeedContentEvent>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val event: SharedFlow<FeedContentEvent> = _event

    fun fetchContents(content: Array<Content>) {
        _contents.update {
            content.toList()
        }
        _loadingStatus.update{
            content.map { false }
        }
    }

    fun fetchIsLoading(position: Int) {
        _isLoading.update {
            loadingStatus.value[position]
        }
    }

    fun fetchCurrentPage(page: Int) {
        _currentPage.update {
            page
        }
    }

    fun resetIsLoading() {
        _isLoading.update { false }
    }

    private fun changeTouchedStatus() {
        _isTouched.update { it.not() }
    }


    fun resetTouchStatus() {
        _isTouched.update { false }
    }

    private fun changeLoadingStatus(position: Int, status: Boolean) {
        _loadingStatus.update {
            it.mapIndexed { index, boolean ->
                if (index == position) status
                else boolean
            }
        }
        if(position == currentPage.value) {
            _isLoading.update {
                status
            }
        }
    }

    fun saveImage(imageIndex: Int, type: String) {
        viewModelScope.launch {
            changeLoadingStatus(imageIndex, true)
            val content = contents.value.getOrNull(imageIndex)
            if (content == null) {
                _event.emit(
                    FeedContentEvent.ShowMessage(
                        R.string.feed_content_message_image_save_fail
                    )
                )
                changeLoadingStatus(imageIndex, false)
                return@launch
            }
            imageDownloadHelper.saveImage(content, type).catch {
                _event.emit(
                    FeedContentEvent.ShowMessage(
                        R.string.feed_content_message_image_save_failure
                    )
                )
                changeLoadingStatus(imageIndex, false)
            }.filter {
                it.state.isFinished
            }.first {
                if (it.state == WorkInfo.State.SUCCEEDED) {
                    if (it.tags.contains(ImageDownloadWorker.TYPE_DOWNLOAD_MANAGER)) {
                        _event.emit(
                            FeedContentEvent.ShowMessage(
                                R.string.feed_content_message_image_save_start
                            )
                        )
                    } else if (it.tags.contains(ImageDownloadWorker.TYPE_MEDIA_STORE)) {
                        _event.emit(
                            FeedContentEvent.ShowMessage(
                                when(getContentType(content.mimeType)) {
                                    TYPE_VIDEO -> {
                                        R.string.feed_content_message_video_save_success
                                    }
                                    else -> {
                                        R.string.feed_content_message_image_save_success
                                    }
                                }
                            )
                        )
                    }
                    changeLoadingStatus(imageIndex, false)
                } else if (it.state == WorkInfo.State.FAILED) {
                    _event.emit(
                        FeedContentEvent.ShowMessage(
                            R.string.feed_content_message_image_save_failure
                        )
                    )
                    changeLoadingStatus(imageIndex, false)
                }
                true
            }
        }
    }

    private fun getContentType(mimeType: String): String {
        return if(mimeType.contains(TYPE_VIDEO)) TYPE_VIDEO else TYPE_IMAGE
    }


    override fun onClick() {
        changeTouchedStatus()
    }
}
