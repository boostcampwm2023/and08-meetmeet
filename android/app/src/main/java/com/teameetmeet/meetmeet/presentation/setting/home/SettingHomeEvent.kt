package com.teameetmeet.meetmeet.presentation.setting.home

sealed class SettingHomeEvent {
    data class ShowMessage(val message: String) : SettingHomeEvent()
    data object NavigateToLoginActivity : SettingHomeEvent()
}