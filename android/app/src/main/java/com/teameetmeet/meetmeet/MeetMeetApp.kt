package com.teameetmeet.meetmeet

import android.app.Application
import android.util.Log
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MeetMeetApp : Application() {

    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
        Log.d("test", BuildConfig.KAKAO_NATIVE_APP_KEY)
    }
}