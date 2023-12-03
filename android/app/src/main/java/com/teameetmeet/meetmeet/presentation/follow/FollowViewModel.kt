package com.teameetmeet.meetmeet.presentation.follow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.data.model.UserWithFollowStatus
import com.teameetmeet.meetmeet.data.repository.FollowRepository
import com.teameetmeet.meetmeet.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val followRepository: FollowRepository
) : ViewModel(), OnUserClickListener {

    private val _follower: MutableStateFlow<List<UserWithFollowStatus>> = MutableStateFlow(listOf())
    val follower: StateFlow<List<UserWithFollowStatus>> = _follower

    private val _following: MutableStateFlow<List<UserWithFollowStatus>> =
        MutableStateFlow(listOf())
    val following: StateFlow<List<UserWithFollowStatus>> = _following

    private val _searchKeyword: MutableStateFlow<String> = MutableStateFlow("")

    private val _searchedUser: MutableStateFlow<List<UserWithFollowStatus>> =
        MutableStateFlow(listOf())
    val searchedUser: StateFlow<List<UserWithFollowStatus>> = _searchedUser

    private val _event: MutableSharedFlow<FollowEvent> = MutableSharedFlow()
    val event: SharedFlow<FollowEvent> = _event

    fun updateSearchedUser(actionType: FollowActionType) {
        viewModelScope.launch {
            when (actionType) {
                FollowActionType.FOLLOW -> {
                    userRepository.getUserWithFollowStatus(
                        _searchKeyword.value
                    ).catch {
                        _event.emit(FollowEvent.ShowMessage(R.string.follow_search_fail))
                    }.collectLatest { user ->
                        _searchedUser.update { listOf(user) }
                    }
                }

                else -> {}
            }
        }
    }

    fun updateFollowing(actionType: FollowActionType) {
        viewModelScope.launch {
            when (actionType) {
                FollowActionType.FOLLOW -> {
                    followRepository.getFollowingWithFollowState()
                        .catch {
                            _event.emit(FollowEvent.ShowMessage(R.string.follow_search_following_fail))
                        }.collectLatest { users ->
                            _following.update { users }
                        }
                }

                else -> {}
            }
        }
    }

    fun updateFollower(actionType: FollowActionType) {
        viewModelScope.launch {
            when (actionType) {
                FollowActionType.FOLLOW -> {
                    followRepository.getFollowerWithFollowState()
                        .catch {
                            _event.emit(FollowEvent.ShowMessage(R.string.follow_search_follower_fail))
                        }.collectLatest { users ->
                            _follower.update { users }
                        }
                }

                else -> {}
            }
        }
    }

    fun updateSearchKeyword(keyword: CharSequence?) {
        _searchKeyword.update { keyword.toString() }
    }

    override fun onFollowClick(user: UserWithFollowStatus) {
        viewModelScope.launch {
            followRepository.follow(user.id).catch {
                _event.emit(FollowEvent.ShowMessage(R.string.follow_follow_fail))
            }.collectLatest {
                updateFollowing(FollowActionType.FOLLOW)
                updateFollower(FollowActionType.FOLLOW)
                if (_searchKeyword.value == user.nickname) {
                    _searchedUser.update { listOf() }
                }
            }
        }
    }

    override fun onUnfollowClick(user: UserWithFollowStatus) {
        viewModelScope.launch {
            followRepository.unFollow(user.id).catch {
                _event.emit(FollowEvent.ShowMessage(R.string.follow_unfollow_fail))
            }.collectLatest {
                updateFollowing(FollowActionType.FOLLOW)
                updateFollower(FollowActionType.FOLLOW)
                if (_searchKeyword.value == user.nickname) {
                    _searchedUser.update { listOf() }
                }
            }
        }
    }

    override fun onInviteEventClick(user: UserWithFollowStatus, id: Int) {
        println("${user.nickname}님을 이벤트 $id 에 초대")
    }

    override fun onInviteGroupClick(user: UserWithFollowStatus, id: Int) {
        println("${user.nickname}님을 그룹 $id 에 초대")
    }
}