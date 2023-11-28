package com.teameetmeet.meetmeet.data.repository

import com.teameetmeet.meetmeet.data.ExpiredRefreshTokenException
import com.teameetmeet.meetmeet.data.local.database.dao.EventDao
import com.teameetmeet.meetmeet.data.model.EventDetail
import com.teameetmeet.meetmeet.data.model.EventStory
import com.teameetmeet.meetmeet.data.network.api.EventStoryApi
import com.teameetmeet.meetmeet.data.network.entity.KakaoLoginRequest
import com.teameetmeet.meetmeet.data.toException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import javax.inject.Inject

class EventStoryRepository @Inject constructor(
    private val eventStoryApi: EventStoryApi,
    private val dao: EventDao
) {

    fun getEventStory(id: Int) : Flow<EventStory> {
        return flowOf(true)
            .map {
               eventStoryApi.getStory(id.toString())
            }.catch {
                throw it.toException()
            }
    }

    fun getEventStoryDetail(id: Int) : Flow<EventDetail> {
        return flowOf(Unit)
            .map {
                eventStoryApi.getStoryDetail(id.toString())
            }.catch {
                throw it.toException()
            }
        //TODO("이벤트 세부 정보 가져오고 로컬에 이벤트가 있으면 색과 알림 가져오기 아니면 DEFAULT 색 일정으로 파싱해서 내리기")
    }

    fun deleteEventStory(id: Int) : Flow<Unit> {
        return flowOf(true)
            .map {
                eventStoryApi.deleteEventStory(id)
            }.catch {
                throw it
                //TODO(예외 처리 필요)
            }
    }

    fun editNotification(message: String) : Flow<Unit> {
        return flowOf(true)
            .map {
                eventStoryApi.editNotification(KakaoLoginRequest(message))
            }.catch {
                throw it
            }
    }

}