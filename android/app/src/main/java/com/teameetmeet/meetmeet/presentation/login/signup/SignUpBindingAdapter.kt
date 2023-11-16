package com.teameetmeet.meetmeet.presentation.login.signup

import android.widget.Button
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout
import com.teameetmeet.meetmeet.presentation.model.EmailState

@BindingAdapter("sign_up_email_state", "duplicate_check")
fun TextInputLayout.bindEmailState(state: EmailState, duplicateCheck: Boolean) {
    when (state) {
        EmailState.Valid -> this.error = null
        EmailState.Invalid -> this.error = "이메일 형식에 맞지 않습니다"
        else -> this.error = null
    }
    if (duplicateCheck) {
        this.helperText = "중복확인 완료"
    } else {
        this.helperText = null
    }
}

@BindingAdapter("email_duplicate_enabled")
fun Button.bindEmailDuplicateEnabled(state: EmailState) {
    this.isEnabled = when (state) {
        EmailState.Valid -> true
        else -> false
    }
}