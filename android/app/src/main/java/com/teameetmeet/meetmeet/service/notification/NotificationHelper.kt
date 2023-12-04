package com.teameetmeet.meetmeet.service.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.presentation.splash.SplashActivity
import com.teameetmeet.meetmeet.service.INTENT_REQUEST_ID_EVENT_NOTIFICATION
import javax.inject.Inject

class NotificationHelper @Inject constructor(private val context: Context) {


    fun createEventNotification(channelId: String, title: String?, content: String, eventId: Int) {

        val notificationIntent = Intent(context, SplashActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            INTENT_REQUEST_ID_EVENT_NOTIFICATION,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        )

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title ?: context.getString(R.string.notification_title_event_notification))
            .setContentText(content)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            try {
                notify(eventId, notificationBuilder.build())
            } catch (_: SecurityException) {
            }
        }
    }

    fun createNotificationChannels() {
        createNotificationChannel(
            R.string.notification_title_event_notification,
            R.string.notification_channel_description_event,
            CHANNEL_ID_EVENT_NOTIFICATION
        )
        createNotificationChannel(
            R.string.notification_title_follow_notification,
            R.string.notification_channel_description_follow,
            CHANNEL_ID_FOLLOW_NOTIFICATION
        )
        createNotificationChannel(
            R.string.notification_title_event_invitation_notification,
            R.string.notification_channel_description_event_invitation,
            CHANNEL_ID_EVENT_INVITATION_NOTIFICATION
        )
    }

    private fun createNotificationChannel(
        notificationNameResId: Int,
        notificationDescriptionResId: Int,
        channelId: String
    ) {
        val name = context.getString(notificationNameResId)
        val descriptionText = context.getString(notificationDescriptionResId)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        const val CHANNEL_ID_EVENT_NOTIFICATION = "channelIdEventNotification"
        const val CHANNEL_ID_FOLLOW_NOTIFICATION = "channelIdFollowNotification"
        const val CHANNEL_ID_EVENT_INVITATION_NOTIFICATION = "channelIdEventInvitationNotification"
    }
}