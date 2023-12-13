package com.teameetmeet.meetmeet.presentation.setting.account

import androidx.annotation.StringRes

sealed class SettingAccountUiEvent {
    data class ShowMessage(@StringRes val message: Int, val extraMessage: String = "") :
        SettingAccountUiEvent()

    data object NavigateToLoginActivity : SettingAccountUiEvent()
}