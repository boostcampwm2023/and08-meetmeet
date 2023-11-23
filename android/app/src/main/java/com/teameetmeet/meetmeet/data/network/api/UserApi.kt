package com.teameetmeet.meetmeet.data.network.api

import com.teameetmeet.meetmeet.data.model.UserProfile
import com.teameetmeet.meetmeet.data.network.entity.AvailableResponse
import com.teameetmeet.meetmeet.data.network.entity.NickNameDuplicationCheckRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserApi {

    @GET("user/info")
    suspend fun getUserProfile(): UserProfile

    @POST()
    fun checkNickNameDuplication(@Body nickNameDuplicationCheckRequest: NickNameDuplicationCheckRequest): AvailableResponse
}