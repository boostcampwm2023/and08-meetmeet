package com.teameetmeet.meetmeet.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.teameetmeet.meetmeet.data.datasource.LocalCalendarDataSource
import com.teameetmeet.meetmeet.service.model.EventAlarm
import com.teameetmeet.meetmeet.util.date.getLocalDateTime
import com.teameetmeet.meetmeet.util.date.toLong
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var localCalendarDataSource: LocalCalendarDataSource

    @Inject
    lateinit var alarmHelper: AlarmHelper

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                alarmHelper.registerRepeatAlarm()
            }

            AlarmHelper.INTENT_ACTION_ALARM_UPDATE -> {
                setAlarms()
            }

            AlarmHelper.INTENT_ACTION_ALARM_EVENT -> {
                // todo noti 보내는 부분 구현 해야함
            }
        }
    }

    private fun setAlarms() {
        CoroutineScope(Dispatchers.IO).launch {
            localCalendarDataSource.getEvents(
                getLocalDateTime().toLong(),
                getLocalDateTime().plusDays(AlarmHelper.UPDATE_DAY_UNIT).toLong()
            )
                .first().let { events ->
                    events.filter {
                        it.getTriggerTime() >= getLocalDateTime().toLong() && it.notification != -1
                    }.map {
                        EventAlarm(
                            id = it.id,
                            triggerTime = it.getTriggerTime(),
                            title = it.title
                        )
                    }.forEach { eventAlarm ->
                        alarmHelper.registerEventAlarm(eventAlarm)
                    }
                }
        }
    }
}