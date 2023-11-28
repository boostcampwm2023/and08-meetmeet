package com.teameetmeet.meetmeet.presentation.eventstory.eventstorydetail

sealed class EventStoryDetailEvent{
    data class ShowMessage(val messageId: Int, val extraMessage: String = "") : EventStoryDetailEvent()
    data object FinishEventStoryActivity : EventStoryDetailEvent()
    data object NavigateToLoginActivity: EventStoryDetailEvent()
}
