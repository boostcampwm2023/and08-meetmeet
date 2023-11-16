package com.teameetmeet.meetmeet.presentation.login.signup

import android.widget.Button
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout
import com.teameetmeet.meetmeet.presentation.model.EmailState
import com.teameetmeet.meetmeet.presentation.model.PasswordState

@BindingAdapter("sign_up_email_state", "duplicate_check")
fun TextInputLayout.bindEmailState(state: EmailState, duplicateCheck: Boolean) {
    when (state) {
        EmailState.Valid -> {
            if (duplicateCheck) {
                this.helperText = "중복확인 완료"
            } else {
                this.error = null
                this.helperText = null
            }
        }

        EmailState.Invalid -> this.error = "이메일 형식에 맞지 않습니다"
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
        PasswordState.Valid -> this.helperText = "안전한 비밀번호 입니다"
        PasswordState.Invalid -> this.error = "영문자, 숫자, 특수문자 !@#*를 포함한 9자 이상 14자 이하"
        else -> {
            this.error = null
            this.helperText = null
        }
    }
}

@BindingAdapter("sign_up_password_confirm_state")
fun TextInputLayout.bindPasswordConfirmState(state: PasswordState) {
    when (state) {
        PasswordState.Valid -> this.helperText = "비밀번호가 일치합니다"
        PasswordState.Invalid -> this.error = "비밀번호가 일치하지 않습니다"
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