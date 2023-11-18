package com.teameetmeet.meetmeet.data.network.api

import com.teameetmeet.meetmeet.data.network.entity.AutoLoginRequest
import com.teameetmeet.meetmeet.data.network.entity.KakaoLoginRequest
import com.teameetmeet.meetmeet.data.network.entity.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApi {

    @POST("/auth/kakao/login")
    fun loginKakao(@Body kakaoLoginRequest: KakaoLoginRequest): LoginResponse

    @POST()
    fun autoLoginApp(@Body autoLoginRequest: AutoLoginRequest): LoginResponse
}