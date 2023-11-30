package com.teameetmeet.meetmeet.presentation.setting.alarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teameetmeet.meetmeet.data.repository.SettingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingAlarmViewModel @Inject constructor(
    private val settingRepository: SettingRepository
) : ViewModel() {
    private val _isPushAlarmOn = MutableStateFlow<Boolean>(true)
    val isPushAlarmOn: StateFlow<Boolean> = _isPushAlarmOn

    init {
        getStoredAlarmState()
    }

    private fun getStoredAlarmState() {
        viewModelScope.launch(Dispatchers.IO) {
            settingRepository
                .getAlarmState()
                .collectLatest {
                    _isPushAlarmOn.emit(it)
                }
        }
    }

    fun changeAlarmState(isOn: Boolean) {
        viewModelScope.launch {
            settingRepository.storeAlarmState(isOn)
        }
    }
}