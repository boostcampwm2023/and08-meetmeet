package com.teameetmeet.meetmeet.presentation.eventstory.eventstory.eventmember

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.data.ExpiredRefreshTokenException
import com.teameetmeet.meetmeet.data.model.UserStatus
import com.teameetmeet.meetmeet.data.repository.FollowRepository
import com.teameetmeet.meetmeet.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
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

    private val _event = MutableSharedFlow<EventMemberEvent>()
    val event: SharedFlow<EventMemberEvent> = _event

    fun fetchEventMember(nicknameList: List<String>) {
        viewModelScope.launch {
            _uiState.update {
                nicknameList.flatMap {
                    userRepository.getUserWithFollowStatus(it).catch { exception ->
                        println(exception)
                        when (exception) {
                            is ExpiredRefreshTokenException -> {}
                            is UnknownHostException -> {}
                            else -> {}
                        }
                    }.first()
                }
            }
            println(uiState.value.toString())
        }
    }

    override fun onButtonClick(userStatus: UserStatus) {
        viewModelScope.launch {
            userStatus.isFollowed ?: return@launch
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