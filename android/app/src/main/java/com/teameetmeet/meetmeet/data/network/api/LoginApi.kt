package com.teameetmeet.meetmeet.data.network.api

import com.teameetmeet.meetmeet.data.network.entity.AutoLoginRequest
import com.teameetmeet.meetmeet.data.network.entity.EmailDuplicationCheckRequest
import com.teameetmeet.meetmeet.data.network.entity.SingleStringRequest
import com.teameetmeet.meetmeet.data.network.entity.LoginResponse
import com.teameetmeet.meetmeet.data.network.entity.SelfSignRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApi {

    @POST("/auth/kakao/login")
    fun loginKakao(@Body singleStringRequest: SingleStringRequest): LoginResponse

    @POST("/auth/login")
    fun loginSelf(@Body selfSignRequest: SelfSignRequest): LoginResponse

    @POST()
    fun autoLoginApp(@Body autoLoginRequest: AutoLoginRequest): LoginResponse

    @POST()
    fun checkEmailDuplication(@Body emailDuplicationCheckRequest: EmailDuplicationCheckRequest): Boolean

    @POST("/auth/register")
    fun signUp(@Body selfSignRequest: SelfSignRequest): LoginResponse
}