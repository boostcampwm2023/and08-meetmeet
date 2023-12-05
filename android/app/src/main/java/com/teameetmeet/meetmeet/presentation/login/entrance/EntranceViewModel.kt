package com.teameetmeet.meetmeet.presentation.login.entrance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.user.UserApiClient
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.data.repository.CalendarRepository
import com.teameetmeet.meetmeet.data.repository.LoginRepository
import com.teameetmeet.meetmeet.data.repository.UserRepository
import com.teameetmeet.meetmeet.service.alarm.AlarmHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EntranceViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val userRepository: UserRepository,
    private val calendarRepository: CalendarRepository,
    private val alarmHelper: AlarmHelper
) : ViewModel() {

    private val _kakaoLoginEvent = MutableSharedFlow<KakaoLoginEvent>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val kakaoLoginEvent: SharedFlow<KakaoLoginEvent> = _kakaoLoginEvent.asSharedFlow()


    init {
        cancelAlarms()
        deleteLocalData()
    }

    private fun deleteLocalData() {
        viewModelScope.launch {
            try {
                userRepository.resetDataStore().first()
                calendarRepository.deleteEvents()
            } catch (_: Exception) {

            }
        }
    }

    fun loginApp() {
        UserApiClient.instance.me { user, error ->
            viewModelScope.launch {
                if (error != null) {
                    _kakaoLoginEvent.tryEmit(
                        KakaoLoginEvent.ShowMessage(
                            R.string.login_kakao_message_kakao_login_fail,
                            error.message.orEmpty()
                        )
                    )
                } else if (user?.id != null) {
                    loginRepository.loginKakao(user.id!!).catch { exception ->
                        when(exception) {
                            else -> _kakaoLoginEvent.tryEmit(
                                KakaoLoginEvent.ShowMessage(
                                    R.string.login_kakao_message_kakao_login_fail,
                                    exception.message.orEmpty()
                                )
                            )
                        }
                    }.collect {
                        if(it) {
                            _kakaoLoginEvent.emit(KakaoLoginEvent.NavigateToProfileSettingFragment)
                        } else {
                            _kakaoLoginEvent.emit(KakaoLoginEvent.NavigateToHomeActivity(user.id!!))
                        }
                    }

                } else {
                    _kakaoLoginEvent.emit(KakaoLoginEvent.ShowMessage(R.string.login_kakao_message_no_user_data))
                }
            }
        }
    }

    private fun cancelAlarms() {
        viewModelScope.launch {
            alarmHelper.cancelAllAlarms()
        }
    }
}