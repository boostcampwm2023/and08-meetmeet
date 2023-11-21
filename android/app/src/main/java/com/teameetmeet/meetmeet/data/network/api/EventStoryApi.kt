package com.teameetmeet.meetmeet.data.network.api

import com.teameetmeet.meetmeet.data.model.EventStory
import retrofit2.http.GET
import retrofit2.http.Path

interface EventStoryApi {

    @GET("/{id}/feeds")
    fun getStory(@Path("id") id: String): EventStory
}