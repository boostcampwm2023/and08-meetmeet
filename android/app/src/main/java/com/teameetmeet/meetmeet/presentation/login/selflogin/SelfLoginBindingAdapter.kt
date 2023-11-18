package com.teameetmeet.meetmeet.presentation.login.selflogin

import android.widget.Button
import androidx.databinding.BindingAdapter
import com.teameetmeet.meetmeet.presentation.login.signup.EmailState
import com.teameetmeet.meetmeet.presentation.login.signup.PasswordState

@BindingAdapter("self_login_enabled")
fun Button.bindSelfLoginEnabled(state: SelfLoginUiState) {
    this.isEnabled =
        state.emailState == EmailState.ValidForm && state.passwordState == PasswordState.Valid
}