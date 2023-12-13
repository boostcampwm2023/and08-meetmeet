package com.teameetmeet.meetmeet.presentation.login.signup

import androidx.annotation.StringRes

sealed class SignUpUiEvent {
    data object NavigateToProfileSettingFragment : SignUpUiEvent()
    data class ShowMessage(@StringRes val message: Int, val extraMessage: String = "") :
        SignUpUiEvent()
}