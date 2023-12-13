package com.teameetmeet.meetmeet.presentation.notification

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.data.ExpiredRefreshTokenException
import com.teameetmeet.meetmeet.data.network.entity.FollowNotification
import com.teameetmeet.meetmeet.data.repository.UserRepository
import com.teameetmeet.meetmeet.presentation.notification.follow.FollowNotificationItemClickListener
import com.teameetmeet.meetmeet.presentation.notification.follow.FollowNotificationUiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class FollowNotificationViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel(), FollowNotificationItemClickListener {

    private val _followNotificationList = MutableStateFlow<List<FollowNotification>>(emptyList())
    val followNotificationList: StateFlow<List<FollowNotification>> = _followNotificationList

    private val _event: MutableSharedFlow<FollowNotificationUiEvent> = MutableSharedFlow()
    val event: SharedFlow<FollowNotificationUiEvent> = _event

    fun fetchFollowNotificationList() {
        viewModelScope.launch {
            userRepository.getFollowNotification().catch {
                emitExceptionEvent(it, R.string.notification_load_fail)
            }.collectLatest { notifications ->
                _followNotificationList.update { notifications }
            }
        }
    }

    fun onDeleteAll() {
        viewModelScope.launch {
            if (_followNotificationList.value.isNotEmpty()) {
                userRepository.deleteUserNotification(
                    _followNotificationList.value.map { it.inviteId }.joinToString(",")
                ).catch {
                    emitExceptionEvent(it, R.string.notification_delete_fail)
                }.collectLatest {
                    fetchFollowNotificationList()
                }
            }
        }
    }

    override fun onDelete(event: FollowNotification) {
        viewModelScope.launch {
            userRepository.deleteUserNotification(event.inviteId.toString()).catch {
                emitExceptionEvent(it, R.string.notification_delete_fail)
            }.collectLatest {
                fetchFollowNotificationList()
            }
        }
    }

    private suspend fun emitExceptionEvent(e: Throwable, @StringRes message: Int) {
        when (e) {
            is ExpiredRefreshTokenException -> {
                _event.emit(FollowNotificationUiEvent.ShowMessage(R.string.common_message_expired_login))
                _event.emit(FollowNotificationUiEvent.NavigateToLoginActivity)
            }

            is UnknownHostException -> {
                _event.emit(FollowNotificationUiEvent.ShowMessage(R.string.common_message_no_internet))
            }

            else -> {
                _event.emit(FollowNotificationUiEvent.ShowMessage(message))
            }
        }
    }
}