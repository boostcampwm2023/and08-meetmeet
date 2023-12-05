package com.teameetmeet.meetmeet.data.network.api

import com.teameetmeet.meetmeet.data.model.EventStory
import com.teameetmeet.meetmeet.data.model.FeedDetail
import com.teameetmeet.meetmeet.data.model.UsersResponse
import com.teameetmeet.meetmeet.data.model.UserStatus
import com.teameetmeet.meetmeet.data.network.entity.AddEventRequest
import com.teameetmeet.meetmeet.data.network.entity.AddFeedCommentRequest
import com.teameetmeet.meetmeet.data.network.entity.AnnouncementRequest
import com.teameetmeet.meetmeet.data.network.entity.EventInviteRequest
import com.teameetmeet.meetmeet.data.network.entity.EventStoryDetailResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface EventStoryApi {

    @GET("/event/{event_id}/feeds")
    suspend fun getStory(@Path("event_id") id: Int): EventStory

    @GET("/event/{eventId}")
    suspend fun getStoryDetail(@Path("eventId") id: Int): EventStoryDetailResponse

    @PATCH("event/{eventId}/announcement")
    suspend fun editNotification(
        @Path("eventId") eventId: Int,
        @Body announcementRequest: AnnouncementRequest
    )

    @POST("event/schedule/join/{eventId}")
    suspend fun joinEventStory(@Path("eventId") eventId: Int)

    @DELETE("event/{eventId}")
    suspend fun deleteEventStory(
        @Path("eventId") id: String,
        @Query("isAll") isAll: Boolean = false
    )

    @PATCH("event/{eventId}")
    suspend fun editEventStory(
        @Path("eventId") id: String,
        @Query("isAll") isAll: Boolean = false,
        @Body editEventRequest: AddEventRequest
    )

    @Multipart
    @POST("feed")
    suspend fun createFeed(
        @Part("eventId") eventId: RequestBody,
        @Part("memo") memo: RequestBody?,
        @Part contents: List<MultipartBody.Part>?
    )

    @GET("feed/{feedId}")
    suspend fun getFeedDetail(
        @Path("feedId") id: Int
    ): FeedDetail

    @POST("feed/{feedId}/comment")
    suspend fun addFeedComment(
        @Path("feedId") id: Int,
        @Body addFeedCommentRequest: AddFeedCommentRequest
    )

    @GET("event/user/followings")
    suspend fun getFollowingWithEventStatus(@Query("eventId") eventId: Int): UsersResponse

    @GET("event/user/followers")
    suspend fun getFollowerWithEventStatus(@Query("eventId") eventId: Int): UsersResponse

    @GET("event/user/search/{eventId}")
    suspend fun getUserWithEventStatus(
        @Path("eventId") eventId: Int,
        @Query("nickname") nickname: String
    ): UsersResponse

    @POST("event/schedule/invite")
    suspend fun inviteEvent(@Body eventInviteRequest: EventInviteRequest)
}