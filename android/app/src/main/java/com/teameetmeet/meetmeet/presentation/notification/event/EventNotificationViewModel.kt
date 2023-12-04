package com.teameetmeet.meetmeet.presentation.notification.event

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class EventNotificationViewModel: ViewModel() {

    private val _eventNotificationList = MutableStateFlow<List<EventNotification>>(emptyList())
    val eventNotificationList: StateFlow<List<EventNotification>> = _eventNotificationList

    fun fetchEventNotificationList() {
        _eventNotificationList.update {
            listOf(
                EventNotification(eventId =1, inviterProfile = "https://github.com/agfalcon.png", inviterNickName = "K004", eventName = "밋밋 3차 오프라인 미팅"),
                EventNotification(eventId =2, inviterProfile = "https://github.com/p-chanmin.png", inviterNickName = "K016", eventName = "밋밋 1차 오프라인 미팅"),
                EventNotification(eventId =3, inviterProfile = "https://github.com/LeeHaiLim.png",inviterNickName = "K032", eventName = "천하제일 코딩 대회"),
                EventNotification(eventId =4, inviterProfile = "https://github.com/chani1209.png", inviterNickName = "J153", eventName = "밋밋 2차 오프라인 미팅"),
                EventNotification(eventId =5, inviterProfile = "https://github.com/cdj2073.png", inviterNickName = "J156", eventName = "보드게임 동호회")
            )
        }
    }
}

data class EventNotification(
    val eventId: Int,
    val inviterProfile: String,
    val inviterNickName: String,
    val eventName: String,
)