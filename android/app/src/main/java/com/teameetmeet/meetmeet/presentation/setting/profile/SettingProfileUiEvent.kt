package com.teameetmeet.meetmeet.presentation.setting.profile

import androidx.annotation.StringRes

sealed class SettingProfileUiEvent {
    data object NavigateToSettingHomeFragment : SettingProfileUiEvent()
    data class ShowMessage(@StringRes val message: Int, val extraMessage: String = "") :
        SettingProfileUiEvent()

    data object NavigateToLoginActivity : SettingProfileUiEvent()
}
