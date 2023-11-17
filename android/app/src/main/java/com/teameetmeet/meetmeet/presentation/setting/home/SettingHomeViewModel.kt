package com.teameetmeet.meetmeet.presentation.setting.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teameetmeet.meetmeet.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingHomeViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _event = MutableSharedFlow<SettingHomeEvent>()
    val event : SharedFlow<SettingHomeEvent> = _event

    fun logout() {
        viewModelScope.launch {
            userRepository.logout().catch {
                _event.emit(SettingHomeEvent.ShowMessage(it.message.orEmpty()))
            }.collect {
                _event.emit(SettingHomeEvent.NavigateToLoginActivity)
            }
        }
    }
}
