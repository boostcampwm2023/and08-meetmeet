package com.teameetmeet.meetmeet.presentation.notification.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teameetmeet.meetmeet.data.network.entity.EventInvitationNotification
import com.teameetmeet.meetmeet.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventNotificationViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _eventNotificationList =
        MutableStateFlow<List<EventInvitationNotification>>(emptyList())
    val eventNotificationList: StateFlow<List<EventInvitationNotification>> = _eventNotificationList

    fun fetchEventNotificationList() {
        viewModelScope.launch {
            userRepository.getEventInvitationNotification().collectLatest { notifications ->
                _eventNotificationList.update { notifications }
            }
        }
    }
}