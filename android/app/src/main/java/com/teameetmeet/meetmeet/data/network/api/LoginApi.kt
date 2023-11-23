package com.teameetmeet.meetmeet.data.network.api

import com.teameetmeet.meetmeet.data.network.entity.AccessTokenResult
import com.teameetmeet.meetmeet.data.network.entity.AvailableResponse
import com.teameetmeet.meetmeet.data.network.entity.EmailDuplicationCheckRequest
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
    fun loginSelf(@Body selfSignRequest: SelfSignRequest): LoginResponse

    @GET("auth/check/token")
    suspend fun autoLoginApp(@Query("token") accessToken: String): AccessTokenResult

    @POST()
    fun checkEmailDuplication(@Body emailDuplicationCheckRequest: EmailDuplicationCheckRequest): AvailableResponse

    @POST("/auth/register")
    fun signUp(@Body selfSignRequest: SelfSignRequest): LoginResponse
}