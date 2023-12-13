package com.teameetmeet.meetmeet.presentation.follow

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.data.ExpiredRefreshTokenException
import com.teameetmeet.meetmeet.data.model.UserStatus
import com.teameetmeet.meetmeet.data.repository.EventStoryRepository
import com.teameetmeet.meetmeet.data.repository.FollowRepository
import com.teameetmeet.meetmeet.data.repository.UserRepository
import com.teameetmeet.meetmeet.presentation.util.THROTTLE_DURATION
import com.teameetmeet.meetmeet.presentation.util.setClickEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.UnknownHostException
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

    private val _event: MutableSharedFlow<FollowUiEvent> = MutableSharedFlow(
        extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val event: SharedFlow<FollowUiEvent> = _event

    private val _showPlaceholder: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showPlaceholder: StateFlow<Boolean> = _showPlaceholder

    private val _followClickEvent = MutableSharedFlow<UserStatus>()
    private val _unfollowClickEvent = MutableSharedFlow<UserStatus>()
    private val _inviteClickEvent = MutableSharedFlow<Pair<UserStatus, Int>>()

    init {
        setFollowRequestFlow()
        setUnfollowRequestFlow()
        setInviteEventRequestFlow()
    }

    fun updateSearchedUser(actionType: FollowActionType, id: Int? = null) {
        viewModelScope.launch {
            if (_searchKeyword.value.isNotEmpty()) {
                _showPlaceholder.update { true }
                when (actionType) {
                    FollowActionType.FOLLOW -> {
                        userRepository.getUserWithFollowStatus(
                            _searchKeyword.value
                        ).catch {
                            emitExceptionEvent(it, R.string.follow_search_fail)
                            _showPlaceholder.update { false }
                        }.collectLatest { users ->
                            _searchedUser.update { users }
                            _showPlaceholder.update { false }
                        }
                    }

                    FollowActionType.EVENT -> {
                        id?.let {
                            eventStoryRepository.getUserWithEventStatus(it, _searchKeyword.value)
                                .catch {
                                    emitExceptionEvent(it, R.string.follow_search_fail)
                                    _showPlaceholder.update { false }
                                }.collectLatest { users ->
                                    _searchedUser.update { users }
                                    _showPlaceholder.update { false }
                                }
                        }
                    }

                    else -> {
                        _showPlaceholder.update { false }
                    }
                }
            }
        }
    }

    fun updateFollowing(actionType: FollowActionType, id: Int? = null) {
        viewModelScope.launch {
            _showPlaceholder.update { true }
            when (actionType) {
                FollowActionType.FOLLOW -> {
                    followRepository.getFollowingWithFollowState()
                        .catch {
                            emitExceptionEvent(it, R.string.follow_search_following_fail)
                            _showPlaceholder.update { false }
                        }.collectLatest { users ->
                            _following.update { users }
                            _showPlaceholder.update { false }
                        }
                }

                FollowActionType.EVENT -> {
                    id?.let { id ->
                        eventStoryRepository.getFollowingWithEventState(id)
                            .catch {
                                emitExceptionEvent(it, R.string.follow_search_following_fail)
                                _showPlaceholder.update { false }
                            }.collectLatest { users ->
                                _following.update { users }
                                _showPlaceholder.update { false }
                            }
                    }
                }

                else -> {
                    _showPlaceholder.update { false }
                }
            }
        }
    }

    fun updateFollower(actionType: FollowActionType, id: Int? = null) {
        viewModelScope.launch {
            _showPlaceholder.update { true }
            when (actionType) {
                FollowActionType.FOLLOW -> {
                    followRepository.getFollowerWithFollowState()
                        .catch {
                            emitExceptionEvent(it, R.string.follow_search_follower_fail)
                            _showPlaceholder.update { false }
                        }.collectLatest { users ->
                            _follower.update { users }
                            _showPlaceholder.update { false }
                        }
                }

                FollowActionType.EVENT -> {
                    id?.let { id ->
                        eventStoryRepository.getFollowerWithEventState(id)
                            .catch {
                                emitExceptionEvent(it, R.string.follow_search_follower_fail)
                                _showPlaceholder.update { false }
                            }.collectLatest { users ->
                                _follower.update { users }
                                _showPlaceholder.update { false }
                            }
                    }
                }

                else -> {
                    _showPlaceholder.update { false }
                }
            }
        }
    }

    fun updateSearchKeyword(keyword: CharSequence?) {
        _searchKeyword.update { keyword.toString() }
    }

    private fun setFollowRequestFlow() {
        _followClickEvent.setClickEvent(viewModelScope, THROTTLE_DURATION) { user ->
            _showPlaceholder.update { true }
            followRepository.follow(user.id)
                .catch {
                    emitExceptionEvent(it, R.string.follow_follow_fail)
                    _showPlaceholder.update { false }
                }.collectLatest {
                    updateFollowing(FollowActionType.FOLLOW)
                    updateFollower(FollowActionType.FOLLOW)
                    updateSearchedUser(FollowActionType.FOLLOW)
                    _showPlaceholder.update { false }
                }
        }
    }

    private fun setUnfollowRequestFlow() {
        _unfollowClickEvent.setClickEvent(viewModelScope, THROTTLE_DURATION) { user ->
            _showPlaceholder.update { true }
            followRepository.unFollow(user.id)
                .catch {
                    emitExceptionEvent(it, R.string.follow_unfollow_fail)
                    _showPlaceholder.update { false }
                }.collectLatest {
                    updateFollowing(FollowActionType.FOLLOW)
                    updateFollower(FollowActionType.FOLLOW)
                    updateSearchedUser(FollowActionType.FOLLOW)
                    _showPlaceholder.update { false }
                }
        }
    }

    private fun setInviteEventRequestFlow() {
        _inviteClickEvent.setClickEvent(viewModelScope, THROTTLE_DURATION) {
            val (user, id) = it
            _showPlaceholder.update { true }
            eventStoryRepository.inviteEvent(id, user.id)
                .catch {
                    emitExceptionEvent(it, R.string.event_story_invite_fail)
                    _showPlaceholder.update { false }
                }.collectLatest {
                    updateFollowing(FollowActionType.EVENT, id)
                    updateFollower(FollowActionType.EVENT, id)
                    updateSearchedUser(FollowActionType.EVENT, id)
                    _showPlaceholder.update { false }
                }
        }
    }

    override fun onProfileClick(user: UserStatus) {
        viewModelScope.launch {
            if (!user.isMe) {
                _event.emit(
                    FollowUiEvent.VisitProfile(user.id, user.nickname)
                )
            }
        }
    }

    override fun onFollowClick(user: UserStatus) {
        viewModelScope.launch {
            _followClickEvent.emit(user)
        }
    }

    override fun onUnfollowClick(user: UserStatus) {
        viewModelScope.launch {
            _unfollowClickEvent.emit(user)
        }
    }

    override fun onInviteEventClick(user: UserStatus, id: Int) {
        viewModelScope.launch {
            _inviteClickEvent.emit(Pair(user, id))
        }
    }

    override fun onInviteGroupClick(user: UserStatus, id: Int) {
        println("${user.nickname}님을 그룹 $id 에 초대")
    }

    private suspend fun emitExceptionEvent(e: Throwable, @StringRes message: Int) {
        when (e) {
            is ExpiredRefreshTokenException -> {
                _event.emit(FollowUiEvent.ShowMessage(R.string.common_message_expired_login))
                _event.emit(FollowUiEvent.NavigateToLoginActivity)
            }

            is UnknownHostException -> {
                _event.emit(FollowUiEvent.ShowMessage(R.string.common_message_no_internet))
            }

            else -> {
                _event.emit(FollowUiEvent.ShowMessage(message))
            }
        }
    }
}