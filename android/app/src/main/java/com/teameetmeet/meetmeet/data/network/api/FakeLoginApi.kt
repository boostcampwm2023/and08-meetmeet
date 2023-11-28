package com.teameetmeet.meetmeet.data.network.api

import com.teameetmeet.meetmeet.data.network.entity.AvailableResponse
import com.teameetmeet.meetmeet.data.network.entity.KakaoLoginRequest
import com.teameetmeet.meetmeet.data.network.entity.LoginResponse
import com.teameetmeet.meetmeet.data.network.entity.SelfSignRequest
import kotlin.random.Random

class FakeLoginApi : LoginApi {
    override suspend fun loginKakao(singleStringRequest: KakaoLoginRequest): LoginResponse {
        return LoginResponse("accessToken", "refreshToken")
    }

    override suspend fun loginSelf(selfSignRequest: SelfSignRequest): LoginResponse {
        return LoginResponse("accessToken", "refreshToken")
    }

    override suspend fun checkEmailDuplication(email: String): AvailableResponse {
        if (Random.nextInt() % 2 == 0) {
            throw Exception()
        }
        return AvailableResponse(true)
    }

    override suspend fun signUp(selfSignRequest: SelfSignRequest) {}
}