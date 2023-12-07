package com.teameetmeet.meetmeet.presentation.notification.follow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teameetmeet.meetmeet.data.network.entity.FollowNotification
import com.teameetmeet.meetmeet.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowNotificationViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel(), FollowNotificationItemClickListener {

    private val _followNotificationList = MutableStateFlow<List<FollowNotification>>(emptyList())
    val followNotificationList: StateFlow<List<FollowNotification>> = _followNotificationList

    fun fetchFollowNotificationList() {
        viewModelScope.launch {
            userRepository.getFollowNotification().collectLatest { notifications ->
                _followNotificationList.update { notifications }
            }
        }
    }

    override fun onDelete(event: FollowNotification) {
        viewModelScope.launch {
            userRepository.deleteUserNotification(event.inviteId.toString()).collectLatest {
                fetchFollowNotificationList()
            }
        }
    }
}