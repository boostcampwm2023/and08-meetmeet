package com.teameetmeet.meetmeet.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.user.UserApiClient
import com.teameetmeet.meetmeet.data.repository.TokenRepository
import com.teameetmeet.meetmeet.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository
) : ViewModel() {

    private val _event = MutableSharedFlow<SplashEvent>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val event: SharedFlow<SplashEvent> = _event.asSharedFlow()

    init {
        checkAutoLoginPossible()
    }

    private fun checkAutoLoginPossible() {
        viewModelScope.launch {
            if (AuthApiClient.instance.hasToken()) {
                UserApiClient.instance.accessTokenInfo { _, error ->
                    if (error != null) {
                        _event.tryEmit(SplashEvent.NavigateToLoginActivity)
                    } else {
                        viewModelScope.launch {
                            checkAppToken()
                        }
                    }
                }
            } else {
                checkAppToken()
            }
        }
    }

    private suspend fun checkAppToken() {
        userRepository.getToken().catch {
            _event.tryEmit(SplashEvent.NavigateToLoginActivity)
        }.collect { token ->
            if (token.isNullOrEmpty()) {
                _event.tryEmit(SplashEvent.NavigateToLoginActivity)
            } else {
                autoLoginApp(token)
            }
        }
    }

    private fun autoLoginApp(token: String) {
        viewModelScope.launch {
            tokenRepository.autoLoginApp(token).catch {
                when(it) {
                    is UnknownHostException -> _event.tryEmit(SplashEvent.NavigateToHomeActivity)
                    else -> _event.tryEmit(SplashEvent.NavigateToLoginActivity)
                }
            }.collect {
                _event.tryEmit(SplashEvent.NavigateToHomeActivity)
            }
        }
    }
}