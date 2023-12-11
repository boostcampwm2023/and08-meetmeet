package com.teameetmeet.meetmeet.presentation.setting.home

import androidx.annotation.StringRes

sealed class SettingHomeUiEvent {
    data class ShowMessage(@StringRes val messageId: Int, val extraMessage: String = "") : SettingHomeUiEvent()
    data object NavigateToLoginActivity : SettingHomeUiEvent()
}