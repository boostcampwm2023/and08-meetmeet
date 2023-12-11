package com.teameetmeet.meetmeet.presentation.login.selflogin

sealed class SelfLoginUiEvent {
    data object NavigateToHomeActivity : SelfLoginUiEvent()
    data class ShowMessage(val message: Int, val extraMessage: String = "") : SelfLoginUiEvent()
}
