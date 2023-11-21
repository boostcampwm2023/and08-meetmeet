package com.teameetmeet.meetmeet.presentation.setting.profile

import com.teameetmeet.meetmeet.data.model.UserProfile

data class SettingProfileUiState(
    val currentUserProfile: UserProfile = UserProfile(null, "", ""),
    val profileImage: String? = null,
    val nickname: String = "",
    val duplicatedEnable: Boolean = false,
    val nickNameState: NickNameState = NickNameState.Same,
)
