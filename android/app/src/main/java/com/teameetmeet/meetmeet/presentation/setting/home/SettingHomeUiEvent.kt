package com.teameetmeet.meetmeet.presentation.setting.home

sealed class SettingHomeUiEvent {
    data class ShowMessage(val messageId: Int, val extraMessage: String = "") : SettingHomeUiEvent()
    data object NavigateToLoginActivity : SettingHomeUiEvent()
}