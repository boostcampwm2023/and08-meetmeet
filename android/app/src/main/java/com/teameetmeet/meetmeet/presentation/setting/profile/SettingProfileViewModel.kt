package com.teameetmeet.meetmeet.presentation.setting.profile

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.data.repository.UserRepository
import com.teameetmeet.meetmeet.util.toAbsolutePath
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SettingProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<SettingProfileUiState> =
        MutableStateFlow(SettingProfileUiState())
    val uiState: StateFlow<SettingProfileUiState> = _uiState

    private val _event: MutableSharedFlow<SettingProfileUiEvent> = MutableSharedFlow()
    val event: SharedFlow<SettingProfileUiEvent> = _event

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
                            profileImage = userProfile.profileImage?.toUri(),
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
        _uiState.update { it.copy(profileImage = uri) }
    }

    fun updateEmptyUserProfileImage() {
        _uiState.update { it.copy(profileImage = null) }
    }

    fun checkDuplicate() {
        viewModelScope.launch {
            _showPlaceholder.update { true }
            userRepository.checkNickNameDuplication(_uiState.value.nickname)
                .catch {
                    _event.emit(SettingProfileUiEvent.ShowMessage(R.string.setting_nickname_duplicate_check_invalid))
                }.collectLatest { isAvailable ->
                    _uiState.update {
                        if (isAvailable) {
                            it.copy(
                                duplicatedEnable = false,
                                nickNameState = NickNameState.Valid
                            )
                        } else {
                            it.copy(
                                duplicatedEnable = false,
                                nickNameState = NickNameState.Invalid
                            )
                        }
                    }
                }
            _showPlaceholder.update { false }
        }
    }

    fun patchUserProfile() {
        viewModelScope.launch {
            _showPlaceholder.update { true }
            val imageFile =
                if (_uiState.value.currentUserProfile.profileImage == _uiState.value.profileImage.toString()) {
                    null
                } else {
                    _uiState.value.profileImage?.toAbsolutePath()?.let { File(it) }
                }
            userRepository.patchUserProfile(imageFile, _uiState.value.nickname)
                .catch {
                    _event.emit(SettingProfileUiEvent.ShowMessage(R.string.setting_profile_fail))
                }
                .collectLatest {
                    _event.emit(SettingProfileUiEvent.NavigateToSettingHomeFragment)
                }
            imageFile?.delete()
            _showPlaceholder.update { false }
        }
    }
}