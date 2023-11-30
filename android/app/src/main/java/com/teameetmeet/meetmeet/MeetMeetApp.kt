package com.teameetmeet.meetmeet

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.kakao.sdk.common.KakaoSdk
import com.teameetmeet.meetmeet.service.notification.NotificationHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MeetMeetApp : Application() {

    init {
        instance = this
    }
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val name = getString(R.string.notification_title_event_notification)
        val descriptionText = getString(R.string.notification_channel_description_event)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(NotificationHelper.CHANNEL_ID_EVENT_NOTIFICATION, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        lateinit var instance: MeetMeetApp
        fun applicationContext() : Context {
            return instance.applicationContext
        }
    }
}