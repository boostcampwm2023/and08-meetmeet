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
        // todo 로그인 API 호출
        println(
            """
            email : ${_uiState.value.email}
            password : ${_uiState.value.password}
        """.trimIndent()
        )
        viewModelScope.launch {
            userRepository.login(_uiState.value.email, _uiState.value.password)
                .catch {
                    // 예외 처리
                }.collectLatest {
                    _event.emit(SelfLoginEvent.SelfLoginSuccess)
                }
        }
    }
}