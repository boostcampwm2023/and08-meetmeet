package com.teameetmeet.meetmeet

import android.app.Application
import android.content.Context
import android.util.Log
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MeetMeetApp : Application() {

    init {
        instance = this
    }
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
    }

    companion object {
        lateinit var instance: MeetMeetApp
        fun applicationContext() : Context {
            return instance.applicationContext
        }
    }
}