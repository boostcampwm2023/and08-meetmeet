package com.teameetmeet.meetmeet.presentation.login.selflogin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teameetmeet.meetmeet.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelfLoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _email: MutableStateFlow<String> = MutableStateFlow("")

    private val _password: MutableStateFlow<String> = MutableStateFlow("")

    private val _event: MutableSharedFlow<SelfLoginEvent> = MutableSharedFlow()
    val event: SharedFlow<SelfLoginEvent> = _event

    fun setEmail(email: CharSequence?) {
        _email.update { email.toString() }
    }

    fun setPassword(password: CharSequence?) {
        _password.update { password.toString() }
    }

    fun login() {
        // todo 로그인 API 호출
        println(
            """
            email : ${_email.value}
            password : ${_password.value}
        """.trimIndent()
        )
        viewModelScope.launch {
            userRepository.login(_email.value, _password.value)
                .catch {
                    // 예외 처리
                }.collectLatest {
                    _event.emit(SelfLoginEvent.SelfLoginSuccess)
                }
        }
    }
}