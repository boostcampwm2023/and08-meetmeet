package com.teameetmeet.meetmeet.presentation.notification.group

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update


class GroupNotificationViewModel: ViewModel() {

    private val _groupNotificationList = MutableStateFlow<List<GroupNotification>>(emptyList())
    val groupNotificationList: StateFlow<List<GroupNotification>> = _groupNotificationList

    fun fetchGroupNotificationList() {
        _groupNotificationList.update {
            listOf(
                GroupNotification(groupId =1, inviterProfile = "https://github.com/agfalcon.png", inviterNickName = "K004", groupName = "부스트 캠프 밋밋"),
                GroupNotification(groupId =2, inviterProfile = "https://github.com/p-chanmin.png", inviterNickName = "K016",groupName = "안드로이드 토이 프로젝트 팀"),
                GroupNotification(groupId =3, inviterProfile = "https://github.com/LeeHaiLim.png",inviterNickName = "K032", groupName = "천하제일 코딩 동호회"),
                GroupNotification(groupId =4, inviterProfile = "https://github.com/chani1209.png", inviterNickName = "J153",groupName = "롤 다이아 지향 팟"),
                GroupNotification(groupId =5, inviterProfile = "https://github.com/cdj2073.png", inviterNickName = "J156", groupName = "보드게임 동호회")
            )
        }
    }
}

data class GroupNotification(
    val groupId: Int,
    val inviterProfile: String,
    val inviterNickName: String,
    val groupName: String,
)