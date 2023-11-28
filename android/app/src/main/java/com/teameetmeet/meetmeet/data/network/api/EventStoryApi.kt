package com.teameetmeet.meetmeet.data.network.api

import com.teameetmeet.meetmeet.data.model.EventDetail
import com.teameetmeet.meetmeet.data.model.EventStory
import com.teameetmeet.meetmeet.data.network.entity.KakaoLoginRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface EventStoryApi {

    @GET("/event/{event_id}/feeds")
    suspend fun getStory(@Path("event_id") id: String): EventStory

    @GET("/event/{event_id}")
    suspend fun getStoryDetail(@Path("event_id") id: String): EventDetail

    @POST()
    suspend fun editNotification(@Body singleStringRequest: KakaoLoginRequest)

    @DELETE("event/{id}")
    suspend fun deleteEventStory(@Path("id") id: Int)
}