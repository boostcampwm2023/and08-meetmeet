package com.teameetmeet.meetmeet.presentation.setting.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.user.UserApiClient
import com.teameetmeet.meetmeet.data.model.UserProfile
import com.teameetmeet.meetmeet.data.repository.UserRepository
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
class SettingHomeViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _user: MutableStateFlow<UserProfile> = MutableStateFlow(UserProfile(null, "", ""))
    val user: StateFlow<UserProfile> = _user

    private val _event: MutableSharedFlow<SettingHomeEvent> = MutableSharedFlow()
    val event: SharedFlow<SettingHomeEvent> = _event

    fun fetchUserProfile() {
        viewModelScope.launch {
            userRepository.getUserProfile()
                .catch {
                    // 예외 처리
                }.collect { userProfile ->
                    _user.update { userProfile }
                }
        }
    }

    fun logout() {
        UserApiClient.instance.logout {
            viewModelScope.launch {
                _event.emit(SettingHomeEvent.NavigateToLoginActivity)
            }
        }
    }
}
