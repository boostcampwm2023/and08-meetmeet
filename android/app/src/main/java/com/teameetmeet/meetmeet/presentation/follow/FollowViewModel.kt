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

    private val _follower: MutableStateFlow<List<UserProfile>> = MutableStateFlow(listOf())
    val follower: StateFlow<List<UserProfile>> = _follower

    private val _following: MutableStateFlow<List<UserProfile>> = MutableStateFlow(listOf())
    val following: StateFlow<List<UserProfile>> = _following

    private val _searchKeyword: MutableStateFlow<String> = MutableStateFlow("")
    val searchKeyword: StateFlow<String> = _searchKeyword

    private val _searchedUser: MutableStateFlow<List<UserProfile>> = MutableStateFlow(listOf())
    val searchedUser: StateFlow<List<UserProfile>> = _searchedUser

    init {
        updateFollower()
        updateFollowing()
    }

    fun updateSearchedUser() {
        // TODO 유저 검색 API 갱신
        val tmp = listOf(
            UserProfile(null, _searchKeyword.value, "email@naver.com")
        )
        _searchedUser.update { tmp }
    }

    fun updateFollower() {
        // TODO 팔로워 목록 API 갱신
        val tmp = listOf(
            UserProfile(null, "팔로워1", "email@naver.com"),
            UserProfile(null, "팔로워2", "email@naver.com"),
            UserProfile(null, "팔로워3", "email@naver.com"),
            UserProfile(null, "팔로워4", "email@naver.com"),
            UserProfile(null, "팔로워5", "email@naver.com"),
        )
        _follower.update { tmp }
    }

    fun updateFollowing() {
        // TODO 팔로윙 목록 API 갱신
        val tmp = listOf(
            UserProfile(null, "팔로잉1", "email@naver.com"),
            UserProfile(null, "팔로잉2", "email@naver.com"),
            UserProfile(null, "팔로잉3", "email@naver.com"),
            UserProfile(null, "팔로잉4", "email@naver.com"),
            UserProfile(null, "팔로잉5", "email@naver.com"),
        )
        _following.update { tmp }
    }

    fun updateSearchKeyword(keyword: CharSequence?) {
        _searchKeyword.update { keyword.toString() }
    }
}