package com.teameetmeet.meetmeet.data.network.api

import com.teameetmeet.meetmeet.data.network.entity.AutoLoginRequest
import com.teameetmeet.meetmeet.data.network.entity.KakaoLoginRequest
import com.teameetmeet.meetmeet.data.network.entity.LoginResponse
import kotlin.random.Random

class FakeLoginApi : LoginApi {
    override fun loginKakao(kakaoLoginRequest: KakaoLoginRequest): LoginResponse {
        val case = Random.nextInt()
        if (case % 2 == 0) {
            throw Exception("일시 오류. 다시 시도해보세요")
        }
        return LoginResponse("accessToken", "refreshToken")
    }

    override fun autoLoginApp(autoLoginRequest: AutoLoginRequest): LoginResponse {
        return LoginResponse("accessToken", "refreshToken")
    }
}