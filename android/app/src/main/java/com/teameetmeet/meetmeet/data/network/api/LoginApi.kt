package com.teameetmeet.meetmeet.data.network.api

import com.teameetmeet.meetmeet.data.network.entity.AvailableResponse
import com.teameetmeet.meetmeet.data.network.entity.KakaoLoginRequest
import com.teameetmeet.meetmeet.data.network.entity.LoginResponse
import com.teameetmeet.meetmeet.data.network.entity.SelfSignRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface LoginApi {

    @POST("auth/kakao")
    suspend fun loginKakao(@Body kakaoLoginRequest: KakaoLoginRequest): LoginResponse

    @POST("/auth/login")
    suspend fun loginSelf(@Body selfSignRequest: SelfSignRequest): LoginResponse

    @GET("auth/check/email")
    suspend fun checkEmailDuplication(@Query("email") email: String): AvailableResponse

    @POST("/auth/register")
    suspend fun signUp(@Body selfSignRequest: SelfSignRequest)
}