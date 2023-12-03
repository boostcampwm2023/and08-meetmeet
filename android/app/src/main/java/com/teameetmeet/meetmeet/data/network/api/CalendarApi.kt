package com.teameetmeet.meetmeet.data.network.api

import com.teameetmeet.meetmeet.data.network.entity.AddEventRequest
import com.teameetmeet.meetmeet.data.network.entity.Events
import com.teameetmeet.meetmeet.data.network.entity.SingleEvent
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface CalendarApi {
    @GET("event")
    suspend fun getEvents(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
    ): Events

    @GET("event/search")
    suspend fun searchEvents(
        @Query("keyword") keyword: String?,
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
    ): Events

    @POST("event")
    suspend fun addSingleEvent(
        @Body addEventRequest: AddEventRequest
    ): SingleEvent

    @POST("event")
    suspend fun addRepeatEvent(
        @Body addEventRequest: AddEventRequest
    ): Events
}