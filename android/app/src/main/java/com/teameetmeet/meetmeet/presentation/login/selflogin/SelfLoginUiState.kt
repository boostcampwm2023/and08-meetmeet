package com.teameetmeet.meetmeet.presentation.login.selflogin

import com.teameetmeet.meetmeet.presentation.login.signup.EmailState
import com.teameetmeet.meetmeet.presentation.login.signup.PasswordState

data class SelfLoginUiState(
    val email: String = "",
    val password: String = "",
    val emailState: EmailState = EmailState.None,
    val passwordState: PasswordState = PasswordState.None
)