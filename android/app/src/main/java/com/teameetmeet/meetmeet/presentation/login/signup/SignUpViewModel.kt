package com.teameetmeet.meetmeet.presentation.login.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teameetmeet.meetmeet.data.repository.UserRepository
import com.teameetmeet.meetmeet.presentation.model.EmailState
import com.teameetmeet.meetmeet.presentation.model.PasswordState
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
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<SignUpUiState> = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState

    private val _event: MutableSharedFlow<SignUpEvent> = MutableSharedFlow()
    val event: SharedFlow<SignUpEvent> = _event

    private val _showPlaceholder: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showPlaceholder: StateFlow<Boolean> = _showPlaceholder

    fun setEmail(email: CharSequence?) {
        val emailString = email.toString()
        val state = getEmailState(emailString)
        _uiState.update {
            it.copy(
                email = emailString, emailState = state, emailDuplicateCheck = false
            )
        }
    }

    fun setPassword(password: CharSequence?) {
        val passwordString = password.toString()
        val passwordState = getPasswordState(passwordString)
        val passwordConfirmState =
            getPasswordConfirmState(passwordString, _uiState.value.passwordConfirm)
        _uiState.update {
            it.copy(
                password = passwordString,
                passwordState = passwordState,
                passwordConfirmState = passwordConfirmState
            )
        }
    }

    fun setPasswordConfirm(passwordConfirm: CharSequence?) {
        val passwordConfirmString = passwordConfirm.toString()
        val state = getPasswordConfirmState(_uiState.value.password, passwordConfirmString)
        _uiState.update {
            it.copy(
                passwordConfirm = passwordConfirmString, passwordConfirmState = state
            )
        }
    }

    fun checkDuplicate() {
        // todo email 중복 확인 API 호출
        viewModelScope.launch {
            _showPlaceholder.update { true }
            userRepository.checkEmailDuplicate(_uiState.value.email)
                .catch {
                    // 예외 처리
                    _showPlaceholder.update { false }
                }.collectLatest { result ->
                    _uiState.update { it.copy(emailDuplicateCheck = result) }
                    _showPlaceholder.update { false }
                }
        }
    }

    fun signUp() {
        // todo 회원가입 API 호출
        viewModelScope.launch {
            _showPlaceholder.update { true }
            userRepository.signUp(_uiState.value.email, _uiState.value.password)
                .catch {
                    // 예외 처리
                    _showPlaceholder.update { false }
                }
                .collectLatest {
                    _event.emit(SignUpEvent.SignUpSuccess)
                    _showPlaceholder.update { false }
                }
        }
    }

    private fun getEmailState(email: String): EmailState {
        return when {
            email.isEmpty() -> EmailState.None
            Verification.isEmail(email) -> EmailState.Valid
            else -> EmailState.Invalid
        }
    }

    private fun getPasswordState(password: String): PasswordState {
        return when {
            password.isEmpty() -> PasswordState.None
            Verification.isPassword(password) -> PasswordState.Valid
            else -> PasswordState.Invalid
        }
    }

    private fun getPasswordConfirmState(password: String, passwordConfirm: String): PasswordState {
        return when {
            passwordConfirm.isEmpty() -> PasswordState.None
            password == passwordConfirm -> PasswordState.Valid
            else -> PasswordState.Invalid
        }
    }
}