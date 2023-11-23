package com.teameetmeet.meetmeet.data.network.api

import com.teameetmeet.meetmeet.data.network.entity.AddEventRequest
import com.teameetmeet.meetmeet.data.network.entity.EventResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface CalendarApi {
    @GET("calendar")
    fun getEvents(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
    ): List<EventResponse>

    @GET("calendar/search")
    fun searchEvents(
        @Query("keyword") keyword: String?,
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
    ): List<EventResponse>

    @POST("event")
    suspend fun addEvent(
        @Body addEventRequest: AddEventRequest
    )
}