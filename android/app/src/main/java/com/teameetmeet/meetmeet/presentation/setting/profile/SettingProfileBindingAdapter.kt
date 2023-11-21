package com.teameetmeet.meetmeet.presentation.setting.profile

import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter(
    "setting_profile_nickname_state", "invalid_text", "valid_text"
)
fun TextInputLayout.bindNickNameState(
    state: NickNameState,
    invalidText: String,
    validText: String
) {
    when (state) {
        NickNameState.Invalid -> this.error = invalidText

        NickNameState.Valid -> this.helperText = validText

        else -> {
            this.error = null
            this.helperText = null
        }
    }
}