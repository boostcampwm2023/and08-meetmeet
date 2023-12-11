package com.teameetmeet.meetmeet.presentation.setting.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.data.ExpiredRefreshTokenException
import com.teameetmeet.meetmeet.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class SettingAccountViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _event: MutableSharedFlow<SettingAccountUiEvent> = MutableSharedFlow(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_LATEST
    )
    val event: SharedFlow<SettingAccountUiEvent> = _event

    fun deleteUser() {
        viewModelScope.launch {
            userRepository.deleteUser()
                .catch {
                    emitExceptionEvent(it, R.string.setting_account_delete_fail)
                }
                .collectLatest {
                    _event.emit(SettingAccountUiEvent.NavigateToLoginActivity)
                }
        }
    }

    private suspend fun emitExceptionEvent(e: Throwable, message: Int) {
        when (e) {
            is ExpiredRefreshTokenException -> {
                _event.emit(SettingAccountUiEvent.ShowMessage(R.string.common_message_expired_login))
                _event.emit(SettingAccountUiEvent.NavigateToLoginActivity)
            }

            is UnknownHostException -> {
                _event.emit(SettingAccountUiEvent.ShowMessage(R.string.common_message_no_internet))
            }

            else -> {
                _event.emit(SettingAccountUiEvent.ShowMessage(message))
            }
        }
    }
}