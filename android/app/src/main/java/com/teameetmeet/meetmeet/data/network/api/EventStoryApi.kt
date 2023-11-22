package com.teameetmeet.meetmeet.data.network.api

import com.teameetmeet.meetmeet.data.model.EventStory
import com.teameetmeet.meetmeet.data.network.entity.SingleStringRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface EventStoryApi {

    @GET("/{id}/feeds")
    fun getStory(@Path("id") id: String): EventStory

    @POST()
    fun editNotification(@Body singleStringRequest: SingleStringRequest)
}