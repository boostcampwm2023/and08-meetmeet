package com.teameetmeet.meetmeet.presentation.eventstory.eventstory.eventmember

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teameetmeet.meetmeet.data.ExpiredRefreshTokenException
import com.teameetmeet.meetmeet.data.model.EventMember
import com.teameetmeet.meetmeet.data.model.UserWithFollowStatus
import com.teameetmeet.meetmeet.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class EventMemberViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    private val _uiState = MutableStateFlow<List<UserWithFollowStatus>>(emptyList())
    val uiState: StateFlow<List<UserWithFollowStatus>> = _uiState

    fun fetchEventMember(eventMemberArray: Array<EventMember>) {
        viewModelScope.launch {
            _uiState.update {
                eventMemberArray.map {
                    userRepository.getUserWithFollowStatus(it.nickname).catch {  exception ->
                        when(exception) {
                            is ExpiredRefreshTokenException -> {}
                            is UnknownHostException -> {}
                            else -> {}
                        }
                    }.first()
                }
            }
        }
    }
}