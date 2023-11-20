package com.teameetmeet.meetmeet.presentation.login.entrance

sealed class KakaoLoginEvent {
    data class NavigateToHomeActivity(val id: Long) : KakaoLoginEvent()

    data object NavigateToProfileSettingFragment : KakaoLoginEvent()
    data class ShowMessage(val message: Int, val extraMessage: String = "") : KakaoLoginEvent()
}
