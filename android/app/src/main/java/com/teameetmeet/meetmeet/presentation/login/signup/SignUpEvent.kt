package com.teameetmeet.meetmeet.presentation.login.signup

sealed class SignUpEvent {
    data object NavigateToProfileSettingFragment : SignUpEvent()
    data class ShowMessage(val message: Int, val extraMessage: String = "") : SignUpEvent()
}