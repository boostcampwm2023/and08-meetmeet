package com.teameetmeet.meetmeet.presentation.setting.home

sealed class SettingHomeEvent {
    data class ShowMessage(val messageId: Int, val extraMessage: String = "") : SettingHomeEvent()
    data object NavigateToLoginActivity : SettingHomeEvent()
}