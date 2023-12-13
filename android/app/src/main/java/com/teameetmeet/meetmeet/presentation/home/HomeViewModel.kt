package com.teameetmeet.meetmeet.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.teameetmeet.meetmeet.data.repository.CalendarRepository
import com.teameetmeet.meetmeet.data.repository.UserRepository
import com.teameetmeet.meetmeet.service.alarm.AlarmHelper
import com.teameetmeet.meetmeet.service.alarm.model.EventAlarm
import com.teameetmeet.meetmeet.util.date.getLocalDateTime
import com.teameetmeet.meetmeet.util.date.toLong
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val calendarRepository: CalendarRepository,
    private val alarmHelper: AlarmHelper
) : ViewModel() {

    init {
        setAlarm()
    }

    fun updateFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) return@OnCompleteListener

            val token = task.result

            viewModelScope.launch {
                userRepository.updateFcmToken(token)
            }
        })
    }

    private fun setAlarm() {
        viewModelScope.launch {
            calendarRepository.getSyncedEvents(
                getLocalDateTime().toLong(),
                getLocalDateTime().plusDays(AlarmHelper.UPDATE_DAY_UNIT).toLong()
            ).catch {
            }.collect { events ->
                events.filter {
                    getLocalDateTime().toLong() <= it.getTriggerTime()
                }.forEach {
                    alarmHelper.registerEventAlarm(
                        EventAlarm(
                            it.id,
                            it.getTriggerTime(),
                            it.notification,
                            it.title
                        )
                    )
                }
            }
        }

    }
}