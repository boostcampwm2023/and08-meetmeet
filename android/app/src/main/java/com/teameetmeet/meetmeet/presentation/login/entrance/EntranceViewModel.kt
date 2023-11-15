package com.teameetmeet.meetmeet.presentation.login.entrance

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.teameetmeet.meetmeet.data.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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

    private val _kakaoLoginEvent = MutableSharedFlow<KakaoLoginEvent>()
    val kakaoLoginEvent: SharedFlow<KakaoLoginEvent> = _kakaoLoginEvent.asSharedFlow()

    private val kakaoLoginCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e("KAKAO", "카카오계정으로 로그인 실패", error)
        } else if (token != null) {
            UserApiClient.instance.me { user, error ->
                viewModelScope.launch {
                    if (error != null) {
                        Log.e("KAKAO", "사용자 정보 요청 실패", error)
                    } else if (user?.id != null) {
                        Log.i("KAKAO", "사용자 정보 요청 성공\n회원번호: ${user.id}")
                        loginRepository.loginKakao(user.id!!).catch {
                            _kakaoLoginEvent.emit(KakaoLoginEvent.Failure(it.message.orEmpty()))
                        }.collect {
                            when (it) {
                                200 -> _kakaoLoginEvent.emit(KakaoLoginEvent.Success(user.id!!))
                                //TODO(들어오는 부분 있으면 예외처리 필요)
                            }
                        }
                    } else {
                        Log.e("KAKAO", "사용자 정보 요청 실패", error)
                    }
                }
            }
        }
    }


    fun loginKakao() {
        viewModelScope.launch {
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(application)) {
                UserApiClient.instance.loginWithKakaoTalk(application) { token, error ->
                    if (error != null) {
                        Log.e("KAKAO", "카카오톡으로 로그인 실패", error)
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            return@loginWithKakaoTalk
                        }
                        UserApiClient.instance.loginWithKakaoAccount(
                            application,
                            callback = kakaoLoginCallback
                        )
                    } else if (token != null) {
                        Log.i("KAKAO", "카카오톡으로 로그인 성공 ${token.accessToken}")

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
}