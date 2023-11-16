package com.teameetmeet.meetmeet.presentation.login.selflogin

sealed class SelfLoginEvent {
    data object SelfLoginSuccess : SelfLoginEvent()
}
