package com.teameetmeet.meetmeet.presentation.addevent

sealed class AddEventUiEvent {
    data class ShowMessage(val messageId: Int, val extraMessage: String = "") : AddEventUiEvent()
    data object FinishAddEventActivity : AddEventUiEvent()
    data object NavigateToLoginActivity : AddEventUiEvent()
}