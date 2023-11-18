package com.teameetmeet.meetmeet.presentation.setting.alarm

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingAlarmViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    private val _isPushAlarmOn = MutableStateFlow<Boolean>(false)
    val isPushAlarmOn: StateFlow<Boolean> = _isPushAlarmOn

    init {
        getStoredAlarmState()
    }

    private fun getStoredAlarmState() {
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.data
                .map { preferences -> preferences[IS_PUSH_ALARM_ON] ?: false }
                .collectLatest {
                    _isPushAlarmOn.emit(it)
                }
        }
    }

    fun changeAlarmState(isOn: Boolean) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[IS_PUSH_ALARM_ON] = isOn
            }
        }
    }

    companion object {
        val IS_PUSH_ALARM_ON = booleanPreferencesKey("isPushAlarmOn")
    }
}