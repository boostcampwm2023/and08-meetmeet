package com.teameetmeet.meetmeet.presentation.notification

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class GroupNotificationViewModel : ViewModel() {

    private val _groupNotificationList = MutableStateFlow<List<GroupNotification>>(emptyList())
    val groupNotificationList: StateFlow<List<GroupNotification>> = _groupNotificationList

    fun fetchGroupNotificationList() {

    }
}

data class GroupNotification(
    val groupId: Int,
    val inviterProfile: String,
    val inviterNickName: String,
    val groupName: String,
)