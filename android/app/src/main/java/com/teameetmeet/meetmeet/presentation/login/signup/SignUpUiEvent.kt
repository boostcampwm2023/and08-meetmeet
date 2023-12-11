package com.teameetmeet.meetmeet.presentation.login.signup

sealed class SignUpUiEvent {
    data object NavigateToProfileSettingFragment : SignUpUiEvent()
    data class ShowMessage(val message: Int, val extraMessage: String = "") : SignUpUiEvent()
}