package com.teameetmeet.meetmeet.data.network.api

import com.teameetmeet.meetmeet.data.network.entity.FollowRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Query

interface FollowApi {

    @POST("follow")
    suspend fun follow(@Body followRequest: FollowRequest)

    @DELETE("follow/follow")
    suspend fun unFollow(@Query("userId") userId: Int)
}