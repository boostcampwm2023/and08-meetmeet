package com.teameetmeet.meetmeet.data.network.api

import com.teameetmeet.meetmeet.data.model.UserProfile
import com.teameetmeet.meetmeet.data.network.entity.AccessTokenRequest
import com.teameetmeet.meetmeet.data.network.entity.AvailableResponse
import com.teameetmeet.meetmeet.data.network.entity.PasswordChangeRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Query

interface UserApi {

    @GET("user/info")
    suspend fun getUserProfile(): UserProfile

    @DELETE("user")
    suspend fun deleteUser()

    @GET("auth/check/nickname")
    suspend fun checkNickNameDuplication(@Query("nickname") nickname: String): AvailableResponse

    @PATCH("user/account")
    suspend fun patchPassword(@Body passwordChangeRequest: PasswordChangeRequest): UserProfile
}