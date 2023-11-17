package com.teameetmeet.meetmeet.presentation.login.signup

import android.widget.Button
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter(
    "sign_up_email_state", "invalid_form_text", "invalid_text", "valid_text"
)
fun TextInputLayout.bindEmailState(
    state: EmailState,
    invalidFormText: String,
    invalidText: String,
    validText: String
) {
    when (state) {
        EmailState.InvalidForm -> this.error = invalidFormText

        EmailState.Invalid -> this.error = invalidText

        EmailState.Valid -> this.helperText = validText

        else -> {
            this.error = null
            this.helperText = null
        }
    }
}

@BindingAdapter("email_duplicate_enabled")
fun Button.bindEmailDuplicateEnabled(state: EmailState) {
    this.isEnabled = when (state) {
        EmailState.ValidForm -> true
        else -> false
    }
}

@BindingAdapter("sign_up_password_state", "valid_text", "invalid_text")
fun TextInputLayout.bindPasswordState(
    state: PasswordState, validText: String, invalidText: String
) {
    when (state) {
        PasswordState.Valid -> this.helperText = validText

        PasswordState.Invalid -> this.error = invalidText

        else -> {
            this.error = null
            this.helperText = null
        }
    }
}

@BindingAdapter("sign_up_enabled")
fun Button.bindSignUpEnabled(state: SignUpUiState) {
    this.isEnabled =
        state.emailState == EmailState.Valid && state.passwordState == PasswordState.Valid && state.passwordConfirmState == PasswordState.Valid
}