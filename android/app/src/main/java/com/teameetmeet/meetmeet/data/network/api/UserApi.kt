package com.teameetmeet.meetmeet.data.network.api

import com.teameetmeet.meetmeet.data.model.UserProfile
import com.teameetmeet.meetmeet.data.model.UsersResponse
import com.teameetmeet.meetmeet.data.network.entity.AvailableResponse
import com.teameetmeet.meetmeet.data.network.entity.EventInvitationNotificationResponse
import com.teameetmeet.meetmeet.data.network.entity.FollowNotificationResponse
import com.teameetmeet.meetmeet.data.network.entity.NicknameChangeRequest
import com.teameetmeet.meetmeet.data.network.entity.PasswordChangeRequest
import com.teameetmeet.meetmeet.data.network.entity.TokenRequest
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface UserApi {

    @GET("user/info")
    suspend fun getUserProfile(): UserProfile

    @DELETE("user")
    suspend fun deleteUser()

    @GET("auth/check/nickname")
    suspend fun checkNickNameDuplication(@Query("nickname") nickname: String): AvailableResponse

    @GET("user/search")
    suspend fun getUserWithFollowStatus(@Query("nickname") nickname: String): UsersResponse

    @PATCH("user/account")
    suspend fun patchPassword(@Body passwordChangeRequest: PasswordChangeRequest): UserProfile

    @PATCH("user/nickname")
    suspend fun patchNickname(@Body nicknameChangeRequest: NicknameChangeRequest)

    @Multipart
    @PATCH("user/profile")
    suspend fun updateProfileImage(
        @Part profile: MultipartBody.Part,
    )

    @POST("user/fcm")
    suspend fun updateFcmToken(
        @Body tokenRequest: TokenRequest
    )

    @GET("user/notification")
    suspend fun getFollowNotification(@Query("page") page: String = "follow"): List<FollowNotificationResponse>

    @GET("user/notification")
    suspend fun getEventInvitationNotification(@Query("page") page: String = "invite"): List<EventInvitationNotificationResponse>

    @DELETE("user/notification")
    suspend fun deleteUserNotification(@Query("ids") ids: String)
}