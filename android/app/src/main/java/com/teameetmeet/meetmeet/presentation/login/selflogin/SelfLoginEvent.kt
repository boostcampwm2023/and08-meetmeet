package com.teameetmeet.meetmeet.presentation.login.selflogin

sealed class SelfLoginEvent {
    data object NavigateToHomeActivity : SelfLoginEvent()
    data class ShowMessage(val message: Int, val extraMessage: String = "") : SelfLoginEvent()
}
