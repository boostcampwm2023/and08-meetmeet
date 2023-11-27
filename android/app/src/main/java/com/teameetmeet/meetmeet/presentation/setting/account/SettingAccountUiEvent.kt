package com.teameetmeet.meetmeet.presentation.setting.account

sealed class SettingAccountUiEvent {
    data class ShowMessage(val message: Int, val extraMessage: String = "") :
        SettingAccountUiEvent()

    data object NavigateToLoginActivity : SettingAccountUiEvent()
}