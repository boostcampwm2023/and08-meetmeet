package com.teameetmeet.meetmeet.presentation.addevent

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.teameetmeet.meetmeet.data.datasource.LocalCalendarDataSource
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

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            println("android.intent.action.BOOT_COMPLETED")
            CoroutineScope(Dispatchers.IO).launch {
                localCalendarDataSource.getEvents(
                    getLocalDateTime().toLong(),
                    getLocalDateTime().plusDays(4).toLong()
                )
                    .first().let { events ->
                        val alarmMgr =
                            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        events.filter {
                            it.startDateTime >= getLocalDateTime().toLong()
                        }.forEach { event ->
                            val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
                                putExtra("title", event.title)
                            }
                            val pendingIntent = PendingIntent.getBroadcast(
                                context,
                                event.id,
                                alarmIntent,
                                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
                            )

                            try {
                                println("event 등록 : $event")
                                println("pendingIntent : $pendingIntent")
                                alarmMgr.setExactAndAllowWhileIdle(
                                    AlarmManager.RTC_WAKEUP,
                                    event.startDateTime - (event.notification * 60 * 1000),
                                    pendingIntent
                                )
                            } catch (_: SecurityException) {
                                println("Security Exception")
                            }
                        }
                    }
            }
        } else {
            println("title : ${intent.getStringExtra("title")}")
            println("action : ${intent.action}")
            println("intent : ${intent}")
        }
    }
}