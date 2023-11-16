package com.teameetmeet.meetmeet.presentation.splash

sealed class SplashEvent {
    data object Login : SplashEvent()

}