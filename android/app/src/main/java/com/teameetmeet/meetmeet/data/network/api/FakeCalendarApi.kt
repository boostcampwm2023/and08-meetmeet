package com.teameetmeet.meetmeet.data.network.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.teameetmeet.meetmeet.data.network.entity.AddEventRequest
import com.teameetmeet.meetmeet.data.network.entity.EventResponse
import com.teameetmeet.meetmeet.data.network.entity.Events
import java.io.InputStreamReader

class FakeCalendarApi() : CalendarApi {
    private val jsonString by lazy {
        javaClass.classLoader?.getResourceAsStream("assets/dummyEvents.json")
            ?.let { InputStreamReader(it) }?.readText()
    }

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    override suspend fun getEvents(
        startDate: String,
        endDate: String
    ): Events {
        return moshi.adapter(Events::class.java).fromJson(jsonString) ?: Events(emptyList())
    }

    override suspend fun searchEvents(
        keyword: String?,
        startDate: String,
        endDate: String
    ): Events {
        return moshi.adapter(Events::class.java).fromJson(jsonString) ?: Events(emptyList())
    }

    override suspend fun addEvent(addEventRequest: AddEventRequest) {
    }
}