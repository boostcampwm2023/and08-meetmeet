package com.teameetmeet.meetmeet.presentation.profile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class ProfileImageViewModel : ViewModel() {

    private val _imageUrl = MutableStateFlow<String>("")
    val imageUrl: StateFlow<String> = _imageUrl


    fun fetchImageUrl(url: String) {
        _imageUrl.update { url }
    }
}