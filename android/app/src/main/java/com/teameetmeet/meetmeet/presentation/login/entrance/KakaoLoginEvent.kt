package com.teameetmeet.meetmeet.presentation.login.entrance

sealed class KakaoLoginEvent {
    data class Success(val id: Long) : KakaoLoginEvent()
    data class Failure(val message: String) : KakaoLoginEvent()
}
