package com.teameetmeet.meetmeet.presentation.addevent

import com.teameetmeet.meetmeet.presentation.model.EventNotification
import java.time.LocalDateTime

sealed class AddEventUiEvent {
    data class ShowMessage(val messageId: Int, val extraMessage: String = "") : AddEventUiEvent()
    data class AlarmSetting(
        val eventId: Int,
        val start: LocalDateTime,
        val title: String,
        val alarm: EventNotification
    ) : AddEventUiEvent()
}