package com.teameetmeet.meetmeet.presentation.addevent

import androidx.annotation.StringRes

sealed class AddEventUiEvent {
    data class ShowMessage(@StringRes val messageId: Int, val extraMessage: String = "") :
        AddEventUiEvent()

    data object FinishAddEventActivity : AddEventUiEvent()
    data object NavigateToLoginActivity : AddEventUiEvent()
}