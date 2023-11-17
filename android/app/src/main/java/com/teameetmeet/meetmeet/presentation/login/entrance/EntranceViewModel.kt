package com.teameetmeet.meetmeet.presentation.login.entrance

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.teameetmeet.meetmeet.R
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
    private val application: Application,
    private val loginRepository: LoginRepository
) : AndroidViewModel(application) {

    private val _kakaoLoginEvent = MutableSharedFlow<KakaoLoginEvent>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val kakaoLoginEvent: SharedFlow<KakaoLoginEvent> = _kakaoLoginEvent.asSharedFlow()

    private val kakaoLoginCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            _kakaoLoginEvent.tryEmit(
                KakaoLoginEvent.Failure(
                    R.string.login_kakao_message_kakao_login_fail,
                    error.message.orEmpty()
                )
            )
        } else if (token != null) {
            loginApp(token)
        }
    }


    fun loginKakao() {
        viewModelScope.launch {
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(application)) {
                UserApiClient.instance.loginWithKakaoTalk(application) { token, error ->
                    if (error != null) {
                        _kakaoLoginEvent.tryEmit(
                            KakaoLoginEvent.Failure(
                                R.string.login_kakao_message_kakao_login_fail,
                                error.message.orEmpty()
                            )
                        )
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            return@loginWithKakaoTalk
                        }
                        UserApiClient.instance.loginWithKakaoAccount(
                            application,
                            callback = kakaoLoginCallback
                        )
                    } else if (token != null) {
                        loginApp(token)
                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(
                    application,
                    callback = kakaoLoginCallback
                )
            }
        }
    }

    private fun loginApp(token: OAuthToken) {
        UserApiClient.instance.me { user, error ->
            viewModelScope.launch {
                if (error != null) {
                    _kakaoLoginEvent.tryEmit(
                        KakaoLoginEvent.Failure(
                            R.string.login_kakao_message_kakao_login_fail,
                            error.message.orEmpty()
                        )
                    )
                } else if (user?.id != null) {
                    Log.i("KAKAO", "사용자 정보 요청 성공\n회원번호: ${user.id}")
                    loginRepository.loginKakao(user.id!!).catch {
                        _kakaoLoginEvent.tryEmit(
                            KakaoLoginEvent.Failure(
                                R.string.login_kakao_message_kakao_login_fail,
                                it.message.orEmpty()
                            )
                        )
                    }.collect {
                        _kakaoLoginEvent.tryEmit(KakaoLoginEvent.Success(user.id!!))
                    }

                } else {
                    _kakaoLoginEvent.tryEmit(KakaoLoginEvent.Failure(R.string.login_kakao_message_no_user_data))
                }
            }
        }
    }
}