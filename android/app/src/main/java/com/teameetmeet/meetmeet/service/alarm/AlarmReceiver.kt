package com.teameetmeet.meetmeet.service.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.teameetmeet.meetmeet.data.repository.EventStoryRepository
import com.teameetmeet.meetmeet.service.INTENT_REQUEST_ID_EVENT_NOTIFICATION
import com.teameetmeet.meetmeet.service.notification.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var alarmHelper: AlarmHelper

    @Inject
    lateinit var notificationHelper: NotificationHelper

    @Inject
    lateinit var eventStoryRepository: EventStoryRepository

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                alarmHelper.registerRepeatAlarm()
                notificationHelper.createNotificationChannels()
            }

            AlarmHelper.INTENT_ACTION_ALARM_UPDATE -> {
                alarmHelper.setAlarms()
            }

            AlarmHelper.INTENT_ACTION_ALARM_EVENT -> {
                notificationHelper.createNotification(
                    channelId = NotificationHelper.CHANNEL_ID_EVENT_NOTIFICATION,
                    requestCode = INTENT_REQUEST_ID_EVENT_NOTIFICATION,
                    title = intent.getStringExtra(AlarmHelper.INTENT_EXTRA_TITLE),
                    content = intent.getStringExtra(AlarmHelper.INTENT_EXTRA_CONTENT).orEmpty(),
                    id = intent.getIntExtra(AlarmHelper.INTENT_EXTRA_EVENT_ID, 0)
                )
            }

            AlarmHelper.INTENT_ACTION_ACCEPT_INVITE_EVENT -> {
                val accept = intent.getBooleanExtra(AlarmHelper.INTENT_EXTRA_ACCEPT, false)
                val inviteId = intent.getIntExtra(AlarmHelper.INTENT_EXTRA_INVITE_ID, 0)
                val eventId = intent.getIntExtra(AlarmHelper.INTENT_EXTRA_EVENT_ID, 0)

                CoroutineScope(Dispatchers.IO).launch {
                    eventStoryRepository.acceptEventInvite(accept, inviteId, eventId).first()
                }

                NotificationManagerCompat.from(context).cancel(eventId)
                notificationHelper.emitActiveNotificationCount()
            }
        }
    }
}