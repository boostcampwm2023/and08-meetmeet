package com.teameetmeet.meetmeet.presentation.eventstory.eventstory.eventmember

import androidx.lifecycle.ViewModel
import com.teameetmeet.meetmeet.data.model.EventMember
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class EventMemberViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<EventMemberUiState>(EventMemberUiState())
    val uiState: StateFlow<EventMemberUiState> = _uiState

    fun fetchEventMember(eventMemberArray: Array<EventMember>) {
        _uiState.update {
            it.copy(
                eventMember = eventMemberArray.toList().map { eventMember -> EventMemberState(eventMember) }
            )
        }
        println(uiState.value.eventMember.toString())
    }
}