package com.teameetmeet.meetmeet.data.network.api

import com.teameetmeet.meetmeet.data.network.entity.EventResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ServerApi {
    @GET //todo: end point
    fun getEvents(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
    ): List<EventResponse>
}