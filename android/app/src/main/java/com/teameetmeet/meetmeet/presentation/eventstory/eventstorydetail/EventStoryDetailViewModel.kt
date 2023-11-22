package com.teameetmeet.meetmeet.presentation.eventstory.eventstorydetail

import android.util.Log
import androidx.lifecycle.ViewModel
import com.teameetmeet.meetmeet.presentation.model.EventNotification
import com.teameetmeet.meetmeet.presentation.model.EventRepeatTerm
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class EventStoryDetailViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<EventStoryDetailUiState>(EventStoryDetailUiState())
    val uiState: StateFlow<EventStoryDetailUiState> = _uiState

    fun setEventName(name: CharSequence) {
        _uiState.update {
            it.copy(eventName = name.toString())
        }
    }

    fun setEventMemo(memo: CharSequence) {
        _uiState.update {
            it.copy(memo = memo.toString())
        }
    }

    fun setEventOpen(isChecked: Boolean) {
        _uiState.update {
            it.copy(isOpen = isChecked)
        }
    }

    fun setEventJoinable(isChecked: Boolean) {
        _uiState.update {
            it.copy(isJoinable = isChecked)
        }
    }

    fun setEventAlarm(index: Int) {
        _uiState.update {
            it.copy(alarm = EventNotification.values()[index])
        }
        Log.d("test", uiState.value.alarm.toString())
    }

    fun setEventRepeat(index: Int) {
        _uiState.update {
            it.copy(eventRepeat = EventRepeatTerm.values()[index])
        }
        Log.d("test", uiState.value.toString())
    }
}