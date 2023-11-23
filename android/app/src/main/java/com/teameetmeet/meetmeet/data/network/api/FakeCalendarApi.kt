package com.teameetmeet.meetmeet.data.network.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.teameetmeet.meetmeet.data.network.entity.AddEventRequest
import com.teameetmeet.meetmeet.data.network.entity.EventResponse
import java.io.InputStreamReader

class FakeCalendarApi : CalendarApi {
    private val jsonString by lazy {
        javaClass.classLoader?.getResourceAsStream("assets/dummyEvents.json")
            ?.let { InputStreamReader(it) }?.readText()
    }

    private val type = Types.newParameterizedType(List::class.java, EventResponse::class.java)
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    override fun getEvents(startDate: String, endDate: String): List<EventResponse> {
        return moshi.adapter<List<EventResponse>>(type).fromJson(jsonString) ?: emptyList()
    }

    override fun searchEvents(
        keyword: String?,
        startDate: String,
        endDate: String
    ): List<EventResponse> {
        return moshi.adapter<List<EventResponse>>(type).fromJson(jsonString) ?: emptyList()
    }

    override suspend fun addEvent(addEventRequest: AddEventRequest) {
    }
}