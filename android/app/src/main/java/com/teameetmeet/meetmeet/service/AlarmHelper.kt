package com.teameetmeet.meetmeet.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import com.teameetmeet.meetmeet.service.model.EventAlarm
import javax.inject.Inject

class AlarmHelper @Inject constructor(private val context: Context) {

    companion object {
        const val INTENT_ACTION_ALARM_EVENT = "intentActionAlarmEvent"

        const val INTENT_ACTION_ALARM_UPDATE = "intentActionAlarmUpdate"
        const val INTENT_REQUEST_ID_ALARM_UPDATE = -1

        const val INTENT_EXTRA_TITLE = "intentExtraTitle"

        const val UPDATE_DAY_UNIT = 8L
    }

    private val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun registerEventAlarm(event: EventAlarm) {
        val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = INTENT_ACTION_ALARM_EVENT
            putExtra(INTENT_EXTRA_TITLE, event.title)
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
}