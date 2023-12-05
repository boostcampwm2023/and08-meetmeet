package com.teameetmeet.meetmeet.presentation.eventstory.feeddetail.feedcontent

import androidx.lifecycle.ViewModel
import com.teameetmeet.meetmeet.data.model.Content
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class FeedContentViewModel : ViewModel(), ImageClickListener {

    private val _contents = MutableStateFlow<List<Content>>(emptyList())
    val contents: StateFlow<List<Content>> = _contents

    private val _isTouched = MutableStateFlow<Boolean>(false)
    val isTouched: StateFlow<Boolean> = _isTouched

    fun fetchContents(content: Array<Content>) {
        _contents.update {
            content.toList()
        }
    }

    private fun changeTouchedStatus() {
        _isTouched.update {
            it.not()
        }
    }

    override fun onClick() {
        changeTouchedStatus()
    }

    fun resetTouchStatus() {
        _isTouched.update {
            false
        }
    }
}