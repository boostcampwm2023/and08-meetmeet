package com.teameetmeet.meetmeet.presentation.login.signup

import android.widget.Button
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.presentation.model.EmailState
import com.teameetmeet.meetmeet.presentation.model.PasswordState

@BindingAdapter("sign_up_email_state", "duplicate_check")
fun TextInputLayout.bindEmailState(state: EmailState, duplicateCheck: Boolean) {
    when (state) {
        EmailState.Valid -> {
            if (duplicateCheck) {
                this.helperText = context.getString(R.string.sign_up_duplicate_check_complete)
            } else {
                this.error = null
                this.helperText = null
            }
        }

        EmailState.Invalid -> this.error = context.getString(R.string.sign_up_email_invalid)
        else -> {
            this.error = null
            this.helperText = null
        }
    }
}

@BindingAdapter("email_duplicate_enabled")
fun Button.bindEmailDuplicateEnabled(state: EmailState) {
    this.isEnabled = when (state) {
        EmailState.Valid -> true
        else -> false
    }
}

@BindingAdapter("sign_up_password_state")
fun TextInputLayout.bindPasswordState(state: PasswordState) {
    when (state) {
        PasswordState.Valid -> this.helperText = context.getString(R.string.sign_up_safe_password)
        PasswordState.Invalid -> this.error = context.getString(R.string.sign_up_password_invalid)
        else -> {
            this.error = null
            this.helperText = null
        }
    }
}

@BindingAdapter("sign_up_password_confirm_state")
fun TextInputLayout.bindPasswordConfirmState(state: PasswordState) {
    when (state) {
        PasswordState.Valid -> this.helperText =
            context.getString(R.string.sign_up_password_confirm_valid)

        PasswordState.Invalid -> this.error =
            context.getString(R.string.sign_up_password_confirm_invalid)

        else -> {
            this.error = null
            this.helperText = null
        }
    }
}

@BindingAdapter("sign_up_enabled")
fun Button.bindSignUpEnabled(state: SignUpUiState) {
    this.isEnabled = state.emailState == EmailState.Valid &&
            state.passwordState == PasswordState.Valid &&
            state.passwordConfirmState == PasswordState.Valid &&
            state.emailDuplicateCheck
}