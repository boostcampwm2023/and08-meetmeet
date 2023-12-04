package com.teameetmeet.meetmeet.presentation.setting.passwordchange

import com.teameetmeet.meetmeet.presentation.login.signup.PasswordState

data class SettingPasswordChangeUiState(
    val password: String = "",
    val passwordConfirm: String = "",
    val passwordState: PasswordState = PasswordState.None,
    val passwordConfirmState: PasswordState = PasswordState.None,
    val submitEnable: Boolean = false
)
