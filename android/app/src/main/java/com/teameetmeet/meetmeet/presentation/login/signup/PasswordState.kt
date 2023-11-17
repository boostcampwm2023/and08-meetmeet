package com.teameetmeet.meetmeet.presentation.login.signup

sealed class PasswordState {
    data object None : PasswordState()
    data object Invalid : PasswordState()
    data object Valid : PasswordState()
}
