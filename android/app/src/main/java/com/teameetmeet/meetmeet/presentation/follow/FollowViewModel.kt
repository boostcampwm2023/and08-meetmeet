package com.teameetmeet.meetmeet.presentation.follow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.data.model.UserStatus
import com.teameetmeet.meetmeet.data.repository.EventStoryRepository
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
    private val followRepository: FollowRepository,
    private val eventStoryRepository: EventStoryRepository
) : ViewModel(), OnUserClickListener {

    private val _follower: MutableStateFlow<List<UserStatus>> = MutableStateFlow(listOf())
    val follower: StateFlow<List<UserStatus>> = _follower

    private val _following: MutableStateFlow<List<UserStatus>> =
        MutableStateFlow(listOf())
    val following: StateFlow<List<UserStatus>> = _following

    private val _searchKeyword: MutableStateFlow<String> = MutableStateFlow("")

    private val _searchedUser: MutableStateFlow<List<UserStatus>> =
        MutableStateFlow(listOf())
    val searchedUser: StateFlow<List<UserStatus>> = _searchedUser

    private val _event: MutableSharedFlow<FollowEvent> = MutableSharedFlow()
    val event: SharedFlow<FollowEvent> = _event

    fun updateSearchedUser(actionType: FollowActionType, id: Int? = null) {
        viewModelScope.launch {
            when (actionType) {
                FollowActionType.FOLLOW -> {
                    userRepository.getUserWithFollowStatus(
                        _searchKeyword.value
                    ).catch {
                        _event.emit(FollowEvent.ShowMessage(R.string.follow_search_fail))
                    }.collectLatest { users ->
                        _searchedUser.update { users }
                    }
                }

                FollowActionType.EVENT -> {
                    id?.let {
                        eventStoryRepository.getUserWithEventStatus(it, _searchKeyword.value)
                            .catch {
                                _event.emit(FollowEvent.ShowMessage(R.string.follow_search_fail))
                            }.collectLatest { users ->
                                _searchedUser.update { users }
                            }
                    }
                }

                else -> {}
            }
        }
    }

    fun updateFollowing(actionType: FollowActionType, id: Int? = null) {
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

                FollowActionType.EVENT -> {
                    id?.let { id ->
                        eventStoryRepository.getFollowingWithEventState(id)
                            .catch {
                                _event.emit(FollowEvent.ShowMessage(R.string.follow_search_following_fail))
                            }.collectLatest { users ->
                                _following.update { users }
                            }
                    }
                }

                else -> {}
            }
        }
    }

    fun updateFollower(actionType: FollowActionType, id: Int? = null) {
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

                FollowActionType.EVENT -> {
                    id?.let { id ->
                        eventStoryRepository.getFollowerWithEventState(id)
                            .catch {
                                _event.emit(FollowEvent.ShowMessage(R.string.follow_search_follower_fail))
                            }.collectLatest { users ->
                                _follower.update { users }
                            }
                    }
                }

                else -> {}
            }
        }
    }

    fun updateSearchKeyword(keyword: CharSequence?) {
        _searchKeyword.update { keyword.toString() }
    }

    override fun onProfileClick(user: UserStatus) {
        viewModelScope.launch {
            _event.emit(
                FollowEvent.VisitProfile(user.id, user.nickname)
            )
        }
    }

    override fun onFollowClick(user: UserStatus) {
        viewModelScope.launch {
            followRepository.follow(user.id)
                .catch {
                    _event.emit(FollowEvent.ShowMessage(R.string.follow_follow_fail))
                }.collectLatest {
                    updateFollowing(FollowActionType.FOLLOW)
                    updateFollower(FollowActionType.FOLLOW)
                    updateSearchedUser(FollowActionType.FOLLOW)
                }
        }
    }

    override fun onUnfollowClick(user: UserStatus) {
        viewModelScope.launch {
            followRepository.unFollow(user.id)
                .catch {
                    _event.emit(FollowEvent.ShowMessage(R.string.follow_unfollow_fail))
                }.collectLatest {
                    updateFollowing(FollowActionType.FOLLOW)
                    updateFollower(FollowActionType.FOLLOW)
                    updateSearchedUser(FollowActionType.FOLLOW)
                }
        }
    }

    override fun onInviteEventClick(user: UserStatus, id: Int) {
        viewModelScope.launch {
            eventStoryRepository.inviteEvent(id, user.id)
                .catch {
                    _event.emit(FollowEvent.ShowMessage(R.string.event_story_invite_fail))
                }.collectLatest {
                    updateFollowing(FollowActionType.EVENT, id)
                    updateFollower(FollowActionType.EVENT, id)
                    updateSearchedUser(FollowActionType.EVENT, id)
                }
        }
    }

    override fun onInviteGroupClick(user: UserStatus, id: Int) {
        println("${user.nickname}님을 그룹 $id 에 초대")
    }
}