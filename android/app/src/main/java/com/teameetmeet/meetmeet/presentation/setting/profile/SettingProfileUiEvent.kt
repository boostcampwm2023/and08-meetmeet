package com.teameetmeet.meetmeet.presentation.setting.profile

sealed class SettingProfileUiEvent {
    data object NavigateToSettingHomeFragment : SettingProfileUiEvent()
    data class ShowMessage(val message: Int, val extraMessage: String = "") :
        SettingProfileUiEvent()

    data object NavigateToLoginActivity : SettingProfileUiEvent()
}
