package com.teameetmeet.meetmeet.data.network.api

import com.teameetmeet.meetmeet.data.model.FollowUsers
import com.teameetmeet.meetmeet.data.network.entity.FollowRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface FollowApi {

    @POST("follow")
    suspend fun follow(@Body followRequest: FollowRequest)

    @DELETE("follow/follow")
    suspend fun unFollow(@Query("userId") userId: Int)

    @GET("follow/followings")
    suspend fun getFollowingWithFollowStatus(): FollowUsers

    @GET("follow/followers")
    suspend fun getFollowerWithFollowStatus(): FollowUsers
}