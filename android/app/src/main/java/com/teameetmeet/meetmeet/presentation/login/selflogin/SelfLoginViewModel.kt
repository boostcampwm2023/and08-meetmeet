package com.teameetmeet.meetmeet.presentation.login.selflogin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.data.repository.LoginRepository
import com.teameetmeet.meetmeet.presentation.login.signup.EmailState
import com.teameetmeet.meetmeet.presentation.login.signup.PasswordState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
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
class SelfLoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<SelfLoginUiState> = MutableStateFlow(SelfLoginUiState())
    val uiState: StateFlow<SelfLoginUiState> = _uiState

    private val _event: MutableSharedFlow<SelfLoginEvent> = MutableSharedFlow(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val event: SharedFlow<SelfLoginEvent> = _event

    private val _showPlaceholder: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showPlaceholder: StateFlow<Boolean> = _showPlaceholder

    fun updateEmail(email: CharSequence?) {
        val emailString = email.toString()
        val state = if (emailString.isNotEmpty()) EmailState.ValidForm else EmailState.None
        _uiState.update { it.copy(email = email.toString(), emailState = state) }
    }

    fun updatePassword(password: CharSequence?) {
        val passwordString = password.toString()
        val state = if (passwordString.isNotEmpty()) PasswordState.Valid else PasswordState.None
        _uiState.update { it.copy(password = password.toString(), passwordState = state) }
    }

    fun login() {
        viewModelScope.launch {
            _showPlaceholder.update { true }
            loginRepository.loginSelf(_uiState.value.email, _uiState.value.password)
                .catch {
                    emitExceptionEvent(it, R.string.login_message_self_login_fail)
                    _showPlaceholder.update { false }
                }.collectLatest {
                    _event.emit(SelfLoginEvent.NavigateToHomeActivity)
                    _showPlaceholder.update { false }
                }
        }
    }

    private suspend fun emitExceptionEvent(e: Throwable, message: Int) {
        when (e) {
            is UnknownHostException -> {
                _event.emit(SelfLoginEvent.ShowMessage(R.string.common_message_no_internet))
            }

            else -> {
                _event.emit(SelfLoginEvent.ShowMessage(message))
            }
        }
    }
}