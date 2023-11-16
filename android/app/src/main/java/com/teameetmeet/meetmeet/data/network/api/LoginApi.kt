package com.teameetmeet.meetmeet.data.network.api

import com.teameetmeet.meetmeet.data.network.entity.KakaoLoginRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApi {

    @POST("/auth/kakao/login")
    fun loginKakao(@Body kakaoLoginRequest: KakaoLoginRequest): Int
}