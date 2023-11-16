package com.teameetmeet.meetmeet.presentation.login.signup

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor() : ViewModel() {

    private val _email: MutableStateFlow<String> = MutableStateFlow("")

    private val _password: MutableStateFlow<String> = MutableStateFlow("")

    private val _passwordConfirm: MutableStateFlow<String> = MutableStateFlow("")

    fun setEmail(email: CharSequence?) {
        _email.update { email.toString() }
    }

    fun setPassword(password: CharSequence?) {
        _password.update { password.toString() }
    }

    fun setPasswordConfirm(passwordConfirm: CharSequence?) {
        _passwordConfirm.update { passwordConfirm.toString() }
    }

    fun checkDuplicate() {
        // todo email 중복 확인 API 호출
    }

    fun signUp() {
        // todo 회원가입 API 호출
        println(
            """
            email : ${_email.value}
            password : ${_password.value}
            passwordConfirm : ${_passwordConfirm.value}
        """.trimIndent()
        )
    }
}