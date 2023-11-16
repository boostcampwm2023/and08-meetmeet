package com.teameetmeet.meetmeet.presentation.login.signup

import com.teameetmeet.meetmeet.presentation.model.EmailState
import com.teameetmeet.meetmeet.presentation.model.PasswordState

data class SignUpUiState(
    val email: String = "",
    val password: String = "",
    val passwordConfirm: String = "",
    val emailState: EmailState = EmailState.None,
    val emailDuplicateCheck: Boolean = false,
    val passwordState: PasswordState = PasswordState.None,
    val passwordConfirmState: PasswordState = PasswordState.None
)