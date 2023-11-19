package com.teameetmeet.meetmeet.data.network.api

import com.teameetmeet.meetmeet.data.FirstSignIn
import com.teameetmeet.meetmeet.data.network.entity.AutoLoginRequest
import com.teameetmeet.meetmeet.data.network.entity.KakaoLoginRequest
import com.teameetmeet.meetmeet.data.network.entity.LoginResponse
import retrofit2.HttpException
import kotlin.random.Random

class FakeLoginApi : LoginApi {
    override fun loginKakao(kakaoLoginRequest: KakaoLoginRequest): LoginResponse {
        val case = Random.nextInt()
        if (case % 2 == 0) {
            throw FirstSignIn()
        }
        return LoginResponse("accessToken", "refreshToken")
    }

    override fun autoLoginApp(autoLoginRequest: AutoLoginRequest): LoginResponse {
        return LoginResponse("accessToken", "refreshToken")
    }
}