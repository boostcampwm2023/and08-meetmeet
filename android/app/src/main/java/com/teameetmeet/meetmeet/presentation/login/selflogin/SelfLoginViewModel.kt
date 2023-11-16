package com.teameetmeet.meetmeet.presentation.login.selflogin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teameetmeet.meetmeet.data.repository.UserRepository
import com.teameetmeet.meetmeet.presentation.model.EmailState
import com.teameetmeet.meetmeet.presentation.model.PasswordState
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
class SelfLoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<SelfLoginUiState> = MutableStateFlow(SelfLoginUiState())
    val uiState: StateFlow<SelfLoginUiState> = _uiState

    private val _event: MutableSharedFlow<SelfLoginEvent> = MutableSharedFlow()
    val event: SharedFlow<SelfLoginEvent> = _event

    private val _showPlaceholder: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showPlaceholder: StateFlow<Boolean> = _showPlaceholder

    fun setEmail(email: CharSequence?) {
        val emailString = email.toString()
        val state = if (emailString.isNotEmpty()) EmailState.Valid else EmailState.None
        _uiState.update { it.copy(email = email.toString(), emailState = state) }
    }

    fun setPassword(password: CharSequence?) {
        val passwordString = password.toString()
        val state = if (passwordString.isNotEmpty()) PasswordState.Valid else PasswordState.None
        _uiState.update { it.copy(password = password.toString(), passwordState = state) }
    }

    fun login() {
        viewModelScope.launch {
            _showPlaceholder.update { true }
            userRepository.login(_uiState.value.email, _uiState.value.password)
                .catch {
                    // 예외 처리
                    _showPlaceholder.update { false }
                }.collectLatest {
                    _event.emit(SelfLoginEvent.SelfLoginSuccess)
                    _showPlaceholder.update { false }
                }
        }
    }
}