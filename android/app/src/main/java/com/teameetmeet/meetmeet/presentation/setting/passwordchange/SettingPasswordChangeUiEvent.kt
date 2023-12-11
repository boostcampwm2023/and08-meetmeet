package com.teameetmeet.meetmeet.presentation.setting.passwordchange

sealed class SettingPasswordChangeUiEvent {
    data object NavigateToSettingHomeFragment : SettingPasswordChangeUiEvent()
    data class ShowMessage(val message: Int, val extraMessage: String = "") :
        SettingPasswordChangeUiEvent()

    data object NavigateToLoginActivity : SettingPasswordChangeUiEvent()
}