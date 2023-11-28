package com.teameetmeet.meetmeet.data.network.api

import com.teameetmeet.meetmeet.data.model.UserProfile
import com.teameetmeet.meetmeet.data.network.entity.AvailableResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.Part
import retrofit2.http.Query

interface UserApi {

    @GET("user/info")
    suspend fun getUserProfile(): UserProfile

    @DELETE("user")
    suspend fun deleteUser()

    @GET("auth/check/nickname")
    suspend fun checkNickNameDuplication(@Query("nickname") nickname: String): AvailableResponse

    @Multipart
    @PATCH("user/info")
    suspend fun updateUserProfile(
        @Part("nickname") nickname: RequestBody,
        @Part profile: MultipartBody.Part?,
    ): UserProfile
}