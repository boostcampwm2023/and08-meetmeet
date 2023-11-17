package com.teameetmeet.meetmeet.presentation.login.signup

data class SignUpUiState(
    val email: String = "",
    val password: String = "",
    val passwordConfirm: String = "",
    val emailState: EmailState = EmailState.None,
    val passwordState: PasswordState = PasswordState.None,
    val passwordConfirmState: PasswordState = PasswordState.None
)