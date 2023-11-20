package com.teameetmeet.meetmeet.data.network.api

import android.util.Log
import com.teameetmeet.meetmeet.data.FirstSignIn
import com.teameetmeet.meetmeet.data.network.entity.AutoLoginRequest
import com.teameetmeet.meetmeet.data.network.entity.EmailDuplicationCheckRequest
import com.teameetmeet.meetmeet.data.network.entity.KakaoLoginRequest
import com.teameetmeet.meetmeet.data.network.entity.LoginResponse
import com.teameetmeet.meetmeet.data.network.entity.SelfSignRequest
import kotlin.random.Random

class FakeLoginApi : LoginApi {
    override fun loginKakao(kakaoLoginRequest: KakaoLoginRequest): LoginResponse {
        val case = Random.nextInt()
        Log.d("test", case.toString())
        if (case % 2 == 0) {
            throw FirstSignIn("accessToken", "refreshToken")
        }
        return LoginResponse("accessToken", "refreshToken")
    }

    override fun loginSelf(selfSignRequest: SelfSignRequest): LoginResponse {
        return LoginResponse("accessToken", "refreshToken")
    }

    override fun autoLoginApp(autoLoginRequest: AutoLoginRequest): LoginResponse {
        return LoginResponse("accessToken", "refreshToken")
    }

    override fun checkEmailDuplication(emailDuplicationCheckRequest: EmailDuplicationCheckRequest): Boolean {
        if (Random.nextInt() % 2 == 0) {
            throw Exception()
        }
        return true
    }

    override fun signUp(selfSignRequest: SelfSignRequest): LoginResponse {
        return LoginResponse("accessToken", "refreshToken")
    }
}