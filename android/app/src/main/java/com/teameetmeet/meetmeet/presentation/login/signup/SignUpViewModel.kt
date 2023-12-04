package com.teameetmeet.meetmeet.presentation.login.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.data.repository.LoginRepository
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
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<SignUpUiState> = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState

    private val _event: MutableSharedFlow<SignUpEvent> = MutableSharedFlow()
    val event: SharedFlow<SignUpEvent> = _event

    private val _showPlaceholder: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showPlaceholder: StateFlow<Boolean> = _showPlaceholder

    fun updateEmail(email: CharSequence?) {
        val emailString = email.toString()
        val state = getEmailStateOf(emailString)
        _uiState.update {
            it.copy(
                email = emailString, emailState = state
            )
        }
    }

    fun updatePassword(password: CharSequence?) {
        val passwordString = password.toString()
        val passwordState = getPasswordStateOf(passwordString)
        val passwordConfirmState =
            getPasswordConfirmStateOf(passwordString, _uiState.value.passwordConfirm)
        _uiState.update {
            it.copy(
                password = passwordString,
                passwordState = passwordState,
                passwordConfirmState = passwordConfirmState
            )
        }
    }

    fun updatePasswordConfirm(passwordConfirm: CharSequence?) {
        val passwordConfirmString = passwordConfirm.toString()
        val state = getPasswordConfirmStateOf(_uiState.value.password, passwordConfirmString)
        _uiState.update {
            it.copy(
                passwordConfirm = passwordConfirmString, passwordConfirmState = state
            )
        }
    }

    fun checkDuplicate() {
        viewModelScope.launch {
            _showPlaceholder.update { true }
            loginRepository.checkEmailDuplication(_uiState.value.email)
                .catch {
                    _event.emit(SignUpEvent.ShowMessage(R.string.login_app_duplicate_check_fail))
                }.collectLatest { isAvailable ->
                    _uiState.update {
                        if (isAvailable) {
                            it.copy(emailState = EmailState.Valid)
                        } else {
                            it.copy(emailState = EmailState.Invalid)
                        }
                    }
                }
            _showPlaceholder.update { false }
        }
    }

    fun signUp() {
        viewModelScope.launch {
            _showPlaceholder.update { true }
            loginRepository.signUp(_uiState.value.email, _uiState.value.password)
                .catch {
                    _event.emit(SignUpEvent.ShowMessage(R.string.login_message_sign_up_fail))
                    _showPlaceholder.update { false }
                }
                .collectLatest {
                    _event.emit(SignUpEvent.NavigateToProfileSettingFragment)
                    _showPlaceholder.update { false }
                }
        }
    }

    private fun getEmailStateOf(email: String): EmailState {
        return when {
            email.isEmpty() -> EmailState.None
            Verification.isValidEmail(email) -> EmailState.ValidForm
            else -> EmailState.InvalidForm
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
}