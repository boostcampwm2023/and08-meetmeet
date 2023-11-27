package com.teameetmeet.meetmeet.presentation.login.entrance

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.user.UserApiClient
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.data.FirstSignIn
import com.teameetmeet.meetmeet.data.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EntranceViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val _kakaoLoginEvent = MutableSharedFlow<KakaoLoginEvent>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val kakaoLoginEvent: SharedFlow<KakaoLoginEvent> = _kakaoLoginEvent.asSharedFlow()


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
                    Log.i("KAKAO", "사용자 정보 요청 성공\n회원번호: ${user.id}")
                    loginRepository.loginKakao(user.id!!).catch { exception ->
                        when(exception) {
                            is FirstSignIn -> _kakaoLoginEvent.emit(KakaoLoginEvent.NavigateToProfileSettingFragment)
                            else -> _kakaoLoginEvent.tryEmit(
                                KakaoLoginEvent.ShowMessage(
                                    R.string.login_kakao_message_kakao_login_fail,
                                    exception.message.orEmpty()
                                )
                            )
                        }
                    }.collect {
                        _kakaoLoginEvent.emit(KakaoLoginEvent.NavigateToHomeActivity(user.id!!))
                    }

                } else {
                    _kakaoLoginEvent.emit(KakaoLoginEvent.ShowMessage(R.string.login_kakao_message_no_user_data))
                }
            }
        }
    }
}