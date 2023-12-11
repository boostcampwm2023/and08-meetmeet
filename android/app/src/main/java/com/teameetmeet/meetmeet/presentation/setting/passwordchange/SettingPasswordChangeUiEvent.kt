package com.teameetmeet.meetmeet.presentation.setting.passwordchange

import androidx.annotation.StringRes

sealed class SettingPasswordChangeUiEvent {
    data object NavigateToSettingHomeFragment : SettingPasswordChangeUiEvent()
    data class ShowMessage(@StringRes val message: Int, val extraMessage: String = "") :
        SettingPasswordChangeUiEvent()

    data object NavigateToLoginActivity : SettingPasswordChangeUiEvent()
}