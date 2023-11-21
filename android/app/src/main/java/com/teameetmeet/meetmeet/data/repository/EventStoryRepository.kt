package com.teameetmeet.meetmeet.data.repository

import com.teameetmeet.meetmeet.data.model.EventStory
import com.teameetmeet.meetmeet.data.network.api.EventStoryApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class EventStoryRepository @Inject constructor(
    private val eventStoryApi: EventStoryApi
) {

    fun getEventStory(id: Int) : Flow<EventStory> {
        return flowOf(true)
            .map {
               eventStoryApi.getStory(id.toString())
            }.catch {
                throw it
                //TODO("예외 처리 필요")
            }
    }

}