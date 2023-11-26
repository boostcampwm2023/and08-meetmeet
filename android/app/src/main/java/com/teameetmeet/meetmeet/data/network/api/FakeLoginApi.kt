package com.teameetmeet.meetmeet.data.network.api

import android.util.Log
import com.teameetmeet.meetmeet.data.FirstSignIn
import com.teameetmeet.meetmeet.data.network.entity.AccessTokenResult
import com.teameetmeet.meetmeet.data.network.entity.AvailableResponse
import com.teameetmeet.meetmeet.data.network.entity.EmailDuplicationCheckRequest
import com.teameetmeet.meetmeet.data.network.entity.KakaoLoginRequest
import com.teameetmeet.meetmeet.data.network.entity.LoginResponse
import com.teameetmeet.meetmeet.data.network.entity.SelfSignRequest
import kotlin.random.Random

class FakeLoginApi : LoginApi {
    override suspend fun loginKakao(singleStringRequest: KakaoLoginRequest): LoginResponse {
        val case = Random.nextInt()
        Log.d("test", case.toString())
        if (case % 2 == 0) {
            throw FirstSignIn("accessToken", "refreshToken")
        }
        return LoginResponse("accessToken", "refreshToken")
    }

    override suspend fun loginSelf(selfSignRequest: SelfSignRequest): LoginResponse {
        return LoginResponse("accessToken", "refreshToken")
    }

    override suspend fun autoLoginApp(accessToken: String): AccessTokenResult {
        return AccessTokenResult(isVerified = true)
    }

    override suspend fun checkEmailDuplication(email: String): AvailableResponse {
        if (Random.nextInt() % 2 == 0) {
            throw Exception()
        }
        return AvailableResponse(true)
    }

    override suspend fun signUp(selfSignRequest: SelfSignRequest): LoginResponse {
        return LoginResponse("accessToken", "refreshToken")
    }
}