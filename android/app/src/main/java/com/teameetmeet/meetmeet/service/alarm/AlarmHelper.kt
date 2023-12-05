package com.teameetmeet.meetmeet.service.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.service.INTENT_REQUEST_ID_ALARM_UPDATE
import com.teameetmeet.meetmeet.service.alarm.model.EventAlarm
import javax.inject.Inject

class AlarmHelper @Inject constructor(
    private val context: Context,
    private val localCalendarDataSource: LocalCalendarDataSource
) {

    private val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun registerEventAlarm(event: EventAlarm) {
        val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = INTENT_ACTION_ALARM_EVENT
            putExtra(INTENT_EXTRA_TITLE, event.title)
            putExtra(INTENT_EXTRA_EVENT_ID, event.id)
            putExtra(INTENT_EXTRA_CONTENT, context.getString(getNotiContentOf(event)))
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            event.id,
            alarmIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        )

        try {
            alarmMgr.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                event.triggerTime,
                pendingIntent
            )
        } catch (_: SecurityException) {
        }
    }

    private fun getNotiContentOf(event: EventAlarm) : Int {
        return when (event.alarmMinutes) {
            0 -> {
                R.string.alarm_notification_message_on_time
            }

            10 -> {
                R.string.alarm_notification_message_before_10_minutes
            }

            60 -> {
                R.string.alarm_notification_message_before_1_hour
            }

            24 * 60 -> {
                R.string.alarm_notification_message_before_1_day
            }

            24 * 60 * 7 -> {
                R.string.alarm_notification_message_before_1_week
            }

            else -> {
                R.string.alarm_notification_message_unknown
            }
        }
    }

    fun registerRepeatAlarm() {
        val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = INTENT_ACTION_ALARM_UPDATE
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            INTENT_REQUEST_ID_ALARM_UPDATE,
            alarmIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        )

        alarmMgr.setInexactRepeating(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime(),
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    fun setAlarms() {
        CoroutineScope(Dispatchers.IO).launch {
            localCalendarDataSource.getEvents(
                getLocalDateTime().toLong(),
                getLocalDateTime().plusDays(UPDATE_DAY_UNIT).toLong()
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
                        registerEventAlarm(eventAlarm)
                    }
                }
        }
    }

    fun cancelAlarm(eventId: Int) {
        val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = INTENT_ACTION_ALARM_EVENT
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            eventId,
            alarmIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        )

        alarmMgr.cancel(pendingIntent)
    }
}