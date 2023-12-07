package com.teameetmeet.meetmeet.presentation.notification.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.data.model.UserStatus
import com.teameetmeet.meetmeet.data.network.entity.EventInvitationNotification
import com.teameetmeet.meetmeet.data.repository.EventStoryRepository
import com.teameetmeet.meetmeet.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventNotificationViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val eventStoryRepository: EventStoryRepository
) : ViewModel(), EventNotificationItemClickListener {

    private val _eventNotificationList =
        MutableStateFlow<List<EventInvitationNotification>>(emptyList())
    val eventNotificationList: StateFlow<List<EventInvitationNotification>> = _eventNotificationList

    private val _event: MutableSharedFlow<EventNotificationUiEvent> = MutableSharedFlow()
    val event: SharedFlow<EventNotificationUiEvent> = _event

    fun fetchEventNotificationList() {
        viewModelScope.launch {
            userRepository.getEventInvitationNotification().collectLatest { notifications ->
                _eventNotificationList.update { notifications }
            }
        }
    }

    fun acceptEventInvite(accept: Boolean, event: EventInvitationNotification) {
        viewModelScope.launch {
            eventStoryRepository.acceptEventInvite(accept, event.inviteId, event.eventId)
                .catch {
                    // 예외 처리
                }.first()
            fetchEventNotificationList()
        }
    }

    override fun onClick(event: EventInvitationNotification) {
        viewModelScope.launch {
            when (event.status) {
                UserStatus.JOIN_STATUS_PENDING -> {
                    _event.emit(EventNotificationUiEvent.ShowAcceptDialog(event))
                }

                UserStatus.JOIN_STATUS_ACCEPTED -> {
                    _event.emit(EventNotificationUiEvent.ShowMessage(R.string.notification_message_invite_accepted))
                }

                UserStatus.JOIN_STATUS_REJECTED -> {
                    _event.emit(EventNotificationUiEvent.ShowMessage(R.string.notification_message_invite_rejected))
                }

                UserStatus.JOIN_STATUS_EXPIRED -> {
                    _event.emit(EventNotificationUiEvent.ShowMessage(R.string.notification_message_invite_expired))
                }
            }
        }
    }

    override fun onDelete(event: EventInvitationNotification) {
        viewModelScope.launch {
            userRepository.deleteUserNotification(event.inviteId.toString()).collectLatest {
                fetchEventNotificationList()
            }
        }
    }
}