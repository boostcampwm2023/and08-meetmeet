package com.teameetmeet.meetmeet.presentation.login.selflogin

import android.widget.Button
import androidx.databinding.BindingAdapter
import com.teameetmeet.meetmeet.presentation.model.EmailState
import com.teameetmeet.meetmeet.presentation.model.PasswordState

@BindingAdapter("self_login_enabled")
fun Button.bindSelfLoginEnabled(state: SelfLoginUiState) {
    this.isEnabled =
        state.emailState == EmailState.Valid && state.passwordState == PasswordState.Valid
}