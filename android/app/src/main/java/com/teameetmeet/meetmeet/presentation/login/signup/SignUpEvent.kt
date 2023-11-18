package com.teameetmeet.meetmeet.presentation.login.signup

sealed class SignUpEvent {
    data object SignUpSuccess : SignUpEvent()
}
