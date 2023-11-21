package com.teameetmeet.meetmeet.presentation.follow

import androidx.lifecycle.ViewModel
import com.teameetmeet.meetmeet.data.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class FollowViewModel @Inject constructor() : ViewModel() {

    private val _users: MutableStateFlow<List<UserProfile>> = MutableStateFlow(listOf())
    val users: StateFlow<List<UserProfile>> = _users

    fun getFollower() {
        // TODO 팔로워 목록 API 갱신
        val tmp = listOf(
            UserProfile(null, "팔로워1"),
            UserProfile(null, "팔로워2"),
            UserProfile(null, "팔로워3"),
            UserProfile(null, "팔로워4"),
            UserProfile(null, "팔로워5"),
        )
        _users.update { tmp }
    }

    fun getFollowing() {
        // TODO 팔로윙 목록 API 갱신
        val tmp = listOf(
            UserProfile(null, "팔로잉1"),
            UserProfile(null, "팔로잉2"),
            UserProfile(null, "팔로잉3"),
            UserProfile(null, "팔로잉4"),
            UserProfile(null, "팔로잉5"),
        )
        _users.update { tmp }
    }
}