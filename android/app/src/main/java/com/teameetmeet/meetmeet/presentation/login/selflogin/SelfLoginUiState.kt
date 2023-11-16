package com.teameetmeet.meetmeet.presentation.login.selflogin

import com.teameetmeet.meetmeet.presentation.model.EmailState
import com.teameetmeet.meetmeet.presentation.model.PasswordState

data class SelfLoginUiState(
    val email: String = "",
    val password: String = "",
    val emailState: EmailState = EmailState.None,
    val passwordState: PasswordState = PasswordState.None
)