package com.teameetmeet.meetmeet.presentation.login.selflogin

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SelfLoginViewModel @Inject constructor() : ViewModel() {

    private val _email: MutableStateFlow<String> = MutableStateFlow("")

    private val _password: MutableStateFlow<String> = MutableStateFlow("")

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
    }
}