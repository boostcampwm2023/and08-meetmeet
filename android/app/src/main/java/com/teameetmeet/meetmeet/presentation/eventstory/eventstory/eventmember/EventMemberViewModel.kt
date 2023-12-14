package com.teameetmeet.meetmeet.presentation.eventstory.eventstory.eventmember

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.data.ExpiredRefreshTokenException
import com.teameetmeet.meetmeet.data.model.UserStatus
import com.teameetmeet.meetmeet.data.repository.FollowRepository
import com.teameetmeet.meetmeet.data.repository.UserRepository
import com.teameetmeet.meetmeet.presentation.util.THROTTLE_DURATION
import com.teameetmeet.meetmeet.presentation.util.setClickEvent
import com.teameetmeet.meetmeet.presentation.util.throttleFirst
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class EventMemberViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val followRepository: FollowRepository
) : ViewModel(), EventMemberClickListener {


    private val _uiState = MutableStateFlow<List<UserStatus>>(emptyList())
    val uiState: StateFlow<List<UserStatus>> = _uiState

    private val _event = MutableSharedFlow<EventMemberEvent>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val event: SharedFlow<EventMemberEvent> = _event

    private val _clickEvent = MutableSharedFlow<UserStatus>()
    private val clickEvent: SharedFlow<UserStatus> = _clickEvent

    init {
        requestChangeFollowStatus()
    }

    fun fetchEventMember(nicknameList: List<String>) {
        viewModelScope.launch {
            _uiState.update {
                nicknameList.flatMap {
                    userRepository.getUserWithFollowStatus(it).catch { exception ->
                        when (exception) {
                            is ExpiredRefreshTokenException -> {
                                _event.emit(EventMemberEvent.NavigateToLoginActivity)
                            }

                            is UnknownHostException -> {
                                _event.emit(EventMemberEvent.ShowMessage(R.string.common_message_no_internet))
                            }

                            else -> {
                                _event.emit(EventMemberEvent.ShowMessage(R.string.event_member_message_fetch_fail))
                            }
                        }
                    }.first()
                }
            }
        }
    }

    override fun onButtonClick(userStatus: UserStatus) {
        viewModelScope.launch {
            _clickEvent.emit(userStatus)
        }
    }

    private fun requestChangeFollowStatus() {
        clickEvent.setClickEvent(viewModelScope, 2000L) { userStatus ->
            userStatus.isFollowed ?: return@setClickEvent
            if (userStatus.isFollowed) {
                followRepository.unFollow(userStatus.id).catch {
                    _event.emit(EventMemberEvent.ShowMessage(R.string.event_member_message_unfollow_fail))
                }.collect {
                    fetchEventMember(uiState.value.map { it.nickname })
                }
            } else {
                followRepository.follow(userStatus.id).catch {
                    _event.emit(EventMemberEvent.ShowMessage(R.string.event_member_message_follow_fail))
                }.collect {
                    fetchEventMember(uiState.value.map { it.nickname })
                }
            }
        }
    }

    override fun onItemViewClick(userStatus: UserStatus) {
        viewModelScope.launch {
            _event.emit(
                EventMemberEvent.NavigateToVisitCalendarActivity(
                    userStatus.id,
                    userStatus.nickname
                )
            )
        }
    }
}