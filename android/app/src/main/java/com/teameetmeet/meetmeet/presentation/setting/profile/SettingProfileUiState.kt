package com.teameetmeet.meetmeet.presentation.setting.profile

import android.net.Uri
import com.teameetmeet.meetmeet.data.model.UserProfile

data class SettingProfileUiState(
    val currentUserProfile: UserProfile = UserProfile(null, "", ""),
    val profileImage: Uri? = null,
    val nickname: String = "",
    val duplicatedEnable: Boolean = false,
    val nickNameState: NickNameState = NickNameState.Same,
)