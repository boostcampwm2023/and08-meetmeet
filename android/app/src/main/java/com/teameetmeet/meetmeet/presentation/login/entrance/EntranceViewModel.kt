package com.teameetmeet.meetmeet.presentation.login.entrance

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EntranceViewModel @Inject constructor(
    private val application : Application
): AndroidViewModel(application) {

    fun loginKakao() {
        viewModelScope.launch {
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(application)) {
                UserApiClient.instance.loginWithKakaoTalk(application) { token, error ->
                    if (error != null) {
                        Log.e("KAKAO", "카카오톡으로 로그인 실패", error)
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            return@loginWithKakaoTalk
                        }
                        UserApiClient.instance.loginWithKakaoAccount(application, callback = kakaoLoginCallback)
                    } else if (token != null) {
                        Log.i("KAKAO", "카카오톡으로 로그인 성공 ${token.accessToken}")

                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(application, callback = kakaoLoginCallback)
            }
        }
    }

    companion object {
        private val kakaoLoginCallback: ( OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e("KAKAO", "카카오계정으로 로그인 실패", error)
            } else if (token != null) {
                UserApiClient.instance.me { user, error ->
                    if (error != null) {
                        Log.e("KAKAO", "사용자 정보 요청 실패", error)
                    }
                    else if (user != null) {
                        Log.i("KAKAO", "사용자 정보 요청 성공\n회원번호: ${user.id}")
                    }
                }
            }
        }
    }
}