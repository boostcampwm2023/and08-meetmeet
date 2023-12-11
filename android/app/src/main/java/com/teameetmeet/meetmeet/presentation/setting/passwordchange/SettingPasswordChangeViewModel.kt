package com.teameetmeet.meetmeet.presentation.setting.passwordchange

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.data.ExpiredRefreshTokenException
import com.teameetmeet.meetmeet.data.repository.UserRepository
import com.teameetmeet.meetmeet.presentation.login.signup.PasswordState
import com.teameetmeet.meetmeet.util.Verification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class SettingPasswordChangeViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<SettingPasswordChangeUiState> =
        MutableStateFlow(SettingPasswordChangeUiState())
    val uiState: StateFlow<SettingPasswordChangeUiState> = _uiState

    private val _event: MutableSharedFlow<SettingPasswordChangeUiEvent> = MutableSharedFlow()
    val event: SharedFlow<SettingPasswordChangeUiEvent> = _event

    private val _showPlaceholder: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showPlaceholder: StateFlow<Boolean> = _showPlaceholder

    fun updatePassword(password: CharSequence?) {
        val passwordString = password.toString()
        val passwordState = getPasswordStateOf(passwordString)
        val passwordConfirmState =
            getPasswordConfirmStateOf(passwordString, _uiState.value.passwordConfirm)
        _uiState.update {
            it.copy(
                password = passwordString,
                passwordState = passwordState,
                passwordConfirmState = passwordConfirmState,
                submitEnable = passwordState == PasswordState.Valid && passwordConfirmState == PasswordState.Valid
            )
        }
    }

    fun updatePasswordConfirm(passwordConfirm: CharSequence?) {
        val passwordConfirmString = passwordConfirm.toString()
        val state = getPasswordConfirmStateOf(_uiState.value.password, passwordConfirmString)
        _uiState.update {
            it.copy(
                passwordConfirm = passwordConfirmString,
                passwordConfirmState = state,
                submitEnable = _uiState.value.passwordState == PasswordState.Valid && state == PasswordState.Valid
            )
        }
    }

    fun patchPassword() {
        viewModelScope.launch {
            _showPlaceholder.update { true }
            userRepository.patchPassword(_uiState.value.password)
                .catch {
                    emitExceptionEvent(it, R.string.setting_password_change_fail)
                    _showPlaceholder.update { false }
                }.collectLatest {
                    _event.emit(SettingPasswordChangeUiEvent.NavigateToSettingHomeFragment)
                    _showPlaceholder.update { false }
                }
        }
    }

    private fun getPasswordStateOf(password: String): PasswordState {
        return when {
            password.isEmpty() -> PasswordState.None
            Verification.isValidPassword(password) -> PasswordState.Valid
            else -> PasswordState.Invalid
        }
    }

    private fun getPasswordConfirmStateOf(
        password: String,
        passwordConfirm: String
    ): PasswordState {
        return when {
            passwordConfirm.isEmpty() -> PasswordState.None
            password == passwordConfirm -> PasswordState.Valid
            else -> PasswordState.Invalid
        }
    }

    private suspend fun emitExceptionEvent(e: Throwable, @StringRes message: Int) {
        when (e) {
            is ExpiredRefreshTokenException -> {
                _event.emit(SettingPasswordChangeUiEvent.ShowMessage(R.string.common_message_expired_login))
                _event.emit(SettingPasswordChangeUiEvent.NavigateToLoginActivity)
            }

            is UnknownHostException -> {
                _event.emit(SettingPasswordChangeUiEvent.ShowMessage(R.string.common_message_no_internet))
            }

            else -> {
                _event.emit(SettingPasswordChangeUiEvent.ShowMessage(message))
            }
        }
    }
}