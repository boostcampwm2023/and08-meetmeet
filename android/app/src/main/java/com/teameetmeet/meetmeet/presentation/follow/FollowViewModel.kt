package com.teameetmeet.meetmeet.presentation.follow

import androidx.lifecycle.ViewModel
import com.teameetmeet.meetmeet.data.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class FollowViewModel @Inject constructor() : ViewModel(), OnUserClickListener {

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
            UserProfile("https://github.com/LeeHaiLim.png", _searchKeyword.value, "email@naver.com")
        )
        _searchedUser.update { tmp }
    }

    fun updateFollower() {
        // TODO 팔로워 목록 API 갱신
        val tmp = listOf(
            UserProfile("https://github.com/chani1209.png", "차세찬", "email@naver.com"),
            UserProfile("https://github.com/cdj2073.png", "최다정", "email@naver.com"),
            UserProfile("https://github.com/agfalcon.png", "김근범", "email@naver.com"),
            UserProfile("https://github.com/LeeHaiLim.png", "이해림", "email@naver.com"),
            UserProfile("https://github.com/p-chanmin.png", "박찬민", "email@naver.com"),
        )
        _follower.update { tmp }
    }

    fun updateFollowing() {
        // TODO 팔로윙 목록 API 갱신
        val tmp = listOf(
            UserProfile("https://github.com/cdj2073.png", "최다정", "email@naver.com"),
            UserProfile("https://github.com/chani1209.png", "차세찬", "email@naver.com"),
            UserProfile("https://github.com/LeeHaiLim.png", "이해림", "email@naver.com"),
            UserProfile("https://github.com/p-chanmin.png", "박찬민", "email@naver.com"),
            UserProfile("https://github.com/agfalcon.png", "김근범", "email@naver.com"),
        )
        _following.update { tmp }
    }

    fun updateSearchKeyword(keyword: CharSequence?) {
        _searchKeyword.update { keyword.toString() }
    }

    override fun onFollowClick(userProfile: UserProfile) {
        println("${userProfile.nickname}님을 팔로우")
    }

    override fun onUnfollowClick(userProfile: UserProfile) {
        println("${userProfile.nickname}님을 언팔로우")
    }

    override fun onInviteEventClick(userProfile: UserProfile, id: Int) {
        println("${userProfile.nickname}님을 이벤트 $id 에 초대")
    }

    override fun onInviteGroupClick(userProfile: UserProfile, id: Int) {
        println("${userProfile.nickname}님을 그룹 $id 에 초대")
    }
}