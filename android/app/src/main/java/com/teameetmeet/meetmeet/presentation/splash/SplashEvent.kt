package com.teameetmeet.meetmeet.presentation.splash

sealed class SplashEvent {
    data object NavigateToLoginActivity : SplashEvent()
    data object NavigateToHomeActivity : SplashEvent()

}