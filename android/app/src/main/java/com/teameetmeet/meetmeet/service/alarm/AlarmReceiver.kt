package com.teameetmeet.meetmeet.service.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.teameetmeet.meetmeet.data.datasource.LocalCalendarDataSource
import com.teameetmeet.meetmeet.service.alarm.model.EventAlarm
import com.teameetmeet.meetmeet.service.notification.NotificationHelper
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

    @Inject
    lateinit var notificationHelper: NotificationHelper

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                alarmHelper.registerRepeatAlarm()
                println("부팅 후 채널 생성 시작")
                notificationHelper.createNotificationChannels()
            }

            AlarmHelper.INTENT_ACTION_ALARM_UPDATE -> {
                setAlarms()
            }

            AlarmHelper.INTENT_ACTION_ALARM_EVENT -> {
                println("알림 호출")
                notificationHelper.createEventNotification(
                    channelId = NotificationHelper.CHANNEL_ID_EVENT_NOTIFICATION,
                    title = intent.getStringExtra(AlarmHelper.INTENT_EXTRA_TITLE),
                    content = intent.getStringExtra(AlarmHelper.INTENT_EXTRA_CONTENT).orEmpty(),
                    eventId = intent.getIntExtra(AlarmHelper.INTENT_EXTRA_EVENT_ID, 0)
                )
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
                            it.notification,
                            title = it.title
                        )
                    }.forEach { eventAlarm ->
                        alarmHelper.registerEventAlarm(eventAlarm)
                    }
                }
        }
    }
}