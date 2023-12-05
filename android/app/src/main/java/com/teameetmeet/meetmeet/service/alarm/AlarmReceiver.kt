package com.teameetmeet.meetmeet.service.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.teameetmeet.meetmeet.service.INTENT_REQUEST_ID_EVENT_NOTIFICATION
import com.teameetmeet.meetmeet.service.notification.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

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
                alarmHelper.setAlarms()
            }

            AlarmHelper.INTENT_ACTION_ALARM_EVENT -> {
                println("알림 호출")
                notificationHelper.createNotification(
                    channelId = NotificationHelper.CHANNEL_ID_EVENT_NOTIFICATION,
                    requestCode = INTENT_REQUEST_ID_EVENT_NOTIFICATION,
                    title = intent.getStringExtra(AlarmHelper.INTENT_EXTRA_TITLE),
                    content = intent.getStringExtra(AlarmHelper.INTENT_EXTRA_CONTENT).orEmpty(),
                    id = intent.getIntExtra(AlarmHelper.INTENT_EXTRA_EVENT_ID, 0)
                )
            }
        }
    }
}