package com.teameetmeet.meetmeet.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teameetmeet.meetmeet.data.repository.LoginRepository
import com.teameetmeet.meetmeet.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@HiltViewModel
class SplashViewModel(
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
            userRepository.getKakaoToken().collect { token ->
                if(token == null) {
                    checkAppToken()
                } else {
                    autoLoginKakao(token)
                }
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

    private fun autoLoginKakao(token: String) {
        //TODO(카카오 자동 로그인 구현)
    }
}