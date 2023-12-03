package com.teameetmeet.meetmeet.presentation.eventstory.eventstory.eventmember

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teameetmeet.meetmeet.data.ExpiredRefreshTokenException
import com.teameetmeet.meetmeet.data.model.UserWithFollowStatus
import com.teameetmeet.meetmeet.data.repository.FollowRepository
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
    private val userRepository: UserRepository,
    private val followRepository: FollowRepository
): ViewModel(), EventMemberClickListener {

    private val _uiState = MutableStateFlow<List<UserWithFollowStatus>>(emptyList())
    val uiState: StateFlow<List<UserWithFollowStatus>> = _uiState

    fun fetchEventMember(nicknameList: List<String>) {
        viewModelScope.launch {
            _uiState.update {
                nicknameList.map {
                    userRepository.getUserWithFollowStatus(it).catch {  exception ->
                        println(exception)
                        when(exception) {
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

    override fun onClick(userWithFollowStatus: UserWithFollowStatus) {
        println("클릭")
        viewModelScope.launch {
            if(userWithFollowStatus.isFollowed) {
                followRepository.unFollow(userWithFollowStatus.id).catch {
                    println("언팔로우 : $it")
                }.collect{
                    fetchEventMember(uiState.value.map{it.nickname})
                }
            } else {
                followRepository.follow(userWithFollowStatus.id).catch {
                    println("팔로우 : $it")
                }.collect{
                    fetchEventMember(uiState.value.map{it.nickname})
                }
            }
        }
    }
}