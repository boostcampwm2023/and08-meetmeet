package com.teameetmeet.meetmeet.presentation.model

sealed class EmailState {
    data object None : EmailState()
    data object Invalid : EmailState()
    data object Valid : EmailState()
}
