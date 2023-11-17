package com.teameetmeet.meetmeet.presentation.setting.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingHomeViewModel @Inject constructor(

) : ViewModel() {

    fun logout() {
        viewModelScope.launch {

        }
    }
}