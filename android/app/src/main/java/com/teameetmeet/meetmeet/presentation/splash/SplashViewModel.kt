package com.teameetmeet.meetmeet.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.user.UserApiClient
import com.teameetmeet.meetmeet.data.repository.LoginRepository
import com.teameetmeet.meetmeet.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val _event = MutableSharedFlow<SplashEvent>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val event : SharedFlow<SplashEvent> = _event.asSharedFlow()

    init {
        checkAutoLoginPossible()
    }

    private fun checkAutoLoginPossible() {
        viewModelScope.launch {
            if (AuthApiClient.instance.hasToken()) {
                UserApiClient.instance.accessTokenInfo { _, error ->
                    if (error != null) {
                        _event.tryEmit(SplashEvent.Login)
                    }
                    else {
                        viewModelScope.launch {
                            checkAppToken()
                        }
                    }
                }
            }
            else {
                checkAppToken()
            }
        }
    }

    private suspend fun checkAppToken() {
        userRepository.getToken().collect { token ->
            if(token == null) {
                _event.tryEmit(SplashEvent.Login)
            } else {
                autoLoginApp(token)
            }
        }
    }

    private fun autoLoginApp(token: String) {
        viewModelScope.launch {
            loginRepository.autoLoginApp(token).catch {
                _event.tryEmit(SplashEvent.Login)
            }.collect {
                _event.tryEmit(SplashEvent.LoginSuccess)
            }
        }
    }
}