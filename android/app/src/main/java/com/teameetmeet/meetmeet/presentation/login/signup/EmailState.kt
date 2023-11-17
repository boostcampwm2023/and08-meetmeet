package com.teameetmeet.meetmeet.presentation.login.signup

sealed class EmailState {
    data object None : EmailState()
    data object InvalidForm : EmailState()
    data object ValidForm : EmailState()
    data object Invalid : EmailState()
    data object Valid : EmailState()
}
