package com.teameetmeet.meetmeet.presentation.login.selflogin

import androidx.annotation.StringRes

sealed class SelfLoginUiEvent {
    data object NavigateToHomeActivity : SelfLoginUiEvent()
    data class ShowMessage(@StringRes val message: Int, val extraMessage: String = "") :
        SelfLoginUiEvent()
}
