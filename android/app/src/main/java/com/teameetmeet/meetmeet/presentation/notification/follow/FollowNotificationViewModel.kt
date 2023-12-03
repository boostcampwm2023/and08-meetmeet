package com.teameetmeet.meetmeet.presentation.notification.follow

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class FollowNotificationViewModel: ViewModel() {

    private val _followNotificationList = MutableStateFlow<List<FollowNotification>>(emptyList())
    val followNotificationList: StateFlow<List<FollowNotification>> = _followNotificationList

    fun fetchFollowNotificationList() {
        _followNotificationList.update {
            listOf(
                FollowNotification(id =1, profile = "https://github.com/agfalcon.png", followerNickname = "K004"),
                FollowNotification(id =2, profile = "https://github.com/p-chanmin.png", followerNickname = "K016"),
                FollowNotification(id =3, profile = "https://github.com/LeeHaiLim.png", followerNickname = "K032"),
                FollowNotification(id =4, profile = "https://github.com/chani1209.png", followerNickname = "J153"),
                FollowNotification(id =5, profile = "https://github.com/cdj2073.png", followerNickname = "J156")
            )
        }
    }
}

data class FollowNotification(
    val id: Int,
    val profile: String,
    val followerNickname: String
)