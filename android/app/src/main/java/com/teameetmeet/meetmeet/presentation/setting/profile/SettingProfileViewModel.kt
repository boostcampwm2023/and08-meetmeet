package com.teameetmeet.meetmeet.presentation.setting.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teameetmeet.meetmeet.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<SettingProfileUiState> =
        MutableStateFlow(SettingProfileUiState())
    val uiState: StateFlow<SettingProfileUiState> = _uiState

    private val _showPlaceholder: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showPlaceholder: StateFlow<Boolean> = _showPlaceholder

    init {
        fetchUserProfile()
    }

    private fun fetchUserProfile() {
        viewModelScope.launch {
            _showPlaceholder.update { true }
            userRepository.getUserProfile()
                .catch {
                    // 예외 처리
                }.collect { userProfile ->
                    _uiState.update {
                        it.copy(
                            currentUserProfile = userProfile,
                            profileImage = userProfile.profileImage,
                            nickname = userProfile.nickname
                        )
                    }
                }
            _showPlaceholder.update { false }
        }
    }

    fun updateUserNickName(nickname: CharSequence?) {
        val isChanged = _uiState.value.currentUserProfile.nickname != nickname.toString()
        _uiState.update {
            it.copy(
                nickname = nickname.toString(),
                duplicatedEnable = isChanged,
                nickNameState = if (isChanged) {
                    NickNameState.None
                } else {
                    NickNameState.Same
                }
            )
        }
    }

    fun updateUserProfileImage(uri: Uri) {
        _uiState.update { it.copy(profileImage = uri.toString()) }
    }

    fun checkDuplicate() {
        viewModelScope.launch {
            _showPlaceholder.update { true }
            userRepository.checkNickNameDuplication(_uiState.value.nickname)
                .catch {
                    _uiState.update {
                        it.copy(
                            duplicatedEnable = false,
                            nickNameState = NickNameState.Invalid
                        )
                    }

                }.collectLatest {
                    _uiState.update {
                        it.copy(
                            duplicatedEnable = false,
                            nickNameState = NickNameState.Valid
                        )
                    }
                }
            _showPlaceholder.update { false }
        }
    }
}