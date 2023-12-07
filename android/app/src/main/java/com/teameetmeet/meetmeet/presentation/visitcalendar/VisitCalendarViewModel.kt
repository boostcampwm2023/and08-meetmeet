package com.teameetmeet.meetmeet.presentation.visitcalendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teameetmeet.meetmeet.data.model.UserStatus
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
class VisitCalendarViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val followRepository: FollowRepository
) : ViewModel() {
    private val _userProfile = MutableStateFlow<UserStatus>(
        UserStatus(id = -1, nickname = "", profile = "")
    )
    val userProfile: StateFlow<UserStatus> = _userProfile

    private val _profileIsVisible = MutableStateFlow<Boolean>(true)
    val profileIsVisible: StateFlow<Boolean> = _profileIsVisible

    private val _event = MutableSharedFlow<VisitCalendarEvent>()
    val event: SharedFlow<VisitCalendarEvent> = _event

    fun fetchUserProfile(userNickname: String) {
        viewModelScope.launch {
            userRepository.getUserWithFollowStatus(userNickname)
                .catch {}
                .collect { userProfile ->
                    _userProfile.update { userProfile.first() }
                }
        }
    }

    fun onFollowButtonClick() {
        _userProfile.value.isFollowed?.let { followStatus ->
            if (followStatus) unFollowUser()
            else followUser()
        }
    }

    private fun followUser() {
        viewModelScope.launch {
            followRepository.follow(_userProfile.value.id)
                .catch {
                }.collectLatest {
                    fetchUserProfile(_userProfile.value.nickname)
                }
        }
    }

    private fun unFollowUser() {
        viewModelScope.launch {
            followRepository.unFollow(_userProfile.value.id)
                .catch {
                }.collectLatest {
                    fetchUserProfile(_userProfile.value.nickname)
                }
        }
    }

    fun onProfileImageClick() {
        viewModelScope.launch {
            _event.emit(VisitCalendarEvent.NavigateToProfileImageFragment(userProfile.value.profile))
        }
    }

    fun changeProfileStatus(status: Boolean) {
        _profileIsVisible.update { status }
    }
}