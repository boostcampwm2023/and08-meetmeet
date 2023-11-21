package com.teameetmeet.meetmeet.presentation.setting.profile

sealed class NickNameState {
    data object None : NickNameState()
    data object Same : NickNameState()
    data object Invalid : NickNameState()
    data object Valid : NickNameState()
}
