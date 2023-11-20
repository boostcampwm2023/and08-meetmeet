package com.teameetmeet.meetmeet.presentation.login.signup

sealed class SignUpEvent {
    data object SignUpSuccess : SignUpEvent()
    data class ShowMessage(val message: Int, val extraMessage: String = "") : SignUpEvent()
}
