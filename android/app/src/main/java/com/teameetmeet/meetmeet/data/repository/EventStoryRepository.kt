package com.teameetmeet.meetmeet.data.repository

import com.teameetmeet.meetmeet.data.model.EventDetail
import com.teameetmeet.meetmeet.data.model.EventStory
import com.teameetmeet.meetmeet.data.model.FeedDetail
import com.teameetmeet.meetmeet.data.model.UserStatus
import com.teameetmeet.meetmeet.data.network.api.EventStoryApi
import com.teameetmeet.meetmeet.data.network.entity.AddEventRequest
import com.teameetmeet.meetmeet.data.network.entity.AddFeedCommentRequest
import com.teameetmeet.meetmeet.data.network.entity.EventInviteRequest
import com.teameetmeet.meetmeet.data.network.entity.KakaoLoginRequest
import com.teameetmeet.meetmeet.data.toException
import com.teameetmeet.meetmeet.presentation.model.EventColor
import com.teameetmeet.meetmeet.presentation.model.EventNotification
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class EventStoryRepository @Inject constructor(
    private val eventStoryApi: EventStoryApi
) {

    fun getEventStory(id: Int): Flow<EventStory> {
        return flowOf(true)
            .map {
                eventStoryApi.getStory(id.toString())
            }.catch {
                throw it.toException()
            }
    }

    fun getEventStoryDetail(id: Int): Flow<EventDetail> {
        return flowOf(Unit)
            .map {
                eventStoryApi.getStoryDetail(id.toString()).result
            }.catch {
                throw it.toException()
            }
    }

    fun deleteEventStory(id: Int, isAll: Boolean = false): Flow<Unit> {
        return flowOf(true)
            .map {
                eventStoryApi.deleteEventStory(id.toString(), isAll)
            }.catch {
                throw it.toException()
            }
    }

    fun editEventStory(
        eventId: Int,
        isAll: Boolean = false,
        title: String,
        startDate: String,
        endDate: String,
        isJoinable: Boolean,
        isVisible: Boolean,
        memo: String,
        repeatTerm: String?,
        repeatFrequency: Int?,
        repeatEndDate: String?,
        color: EventColor,
        alarm: EventNotification
    ): Flow<Unit> {
        return flowOf(true)
            .map {
                eventStoryApi.editEventStory(
                    id = eventId.toString(),
                    isAll = isAll,
                    AddEventRequest(
                        title = title,
                        startDate = startDate,
                        endDate = endDate,
                        isJoinable = isJoinable,
                        isVisible = isVisible,
                        alarmMinutes = alarm.minutes,
                        memo = memo.ifEmpty { null },
                        color = color.value,
                        repeatTerm = repeatTerm,
                        repeatFrequency = repeatFrequency,
                        repeatEndDate = repeatEndDate
                    )
                )
            }.catch {
                throw it.toException()
            }
    }

    fun editNotification(message: String): Flow<Unit> {
        return flowOf(true)
            .map {
                eventStoryApi.editNotification(KakaoLoginRequest(message))
            }.catch {
                throw it.toException()
            }
    }

    fun createFeed(eventId: Int, memo: String?, media: List<File>?): Flow<Unit> {
        return flowOf(true).map {
            val contents = media?.map {
                MultipartBody.Part.createFormData("contents", it.name, it.asRequestBody())
            }
            eventStoryApi.createFeed(
                eventId.toString().toRequestBody(),
                memo?.toRequestBody(),
                contents
            )
        }.catch {
            //todo: 예외처리
            throw it
        }
    }

    fun joinEventStory(eventId: Int): Flow<Unit> {
        return flowOf(true).map {
            eventStoryApi.joinEventStory(eventId)
        }.catch {
            throw it.toException()
        }
    }

    fun getFeedDetail(feedId: Int): Flow<FeedDetail> {
        return flowOf(true).map {
            eventStoryApi.getFeedDetail(feedId)
        }.catch {
            //todo: 예외처리
            throw it
        }
    }

    fun addFeedComment(feedId: Int, memo: String): Flow<Unit> {
        return flowOf(true).map {
            eventStoryApi.addFeedComment(
                feedId, AddFeedCommentRequest(memo)
            )
        }.catch {
            //todo: 예외처리
            throw it
        }
    }

    fun inviteEvent(eventId: Int, userId: Int): Flow<Unit> {
        return flowOf(true)
            .map {
                val request = EventInviteRequest(userId = userId, eventId = eventId)
                eventStoryApi.inviteEvent(request)
            }.catch {
                throw it
            }
    }

    fun getFollowingWithEventState(eventId: Int): Flow<List<UserStatus>> {
        return flowOf(true)
            .map {
                eventStoryApi.getFollowingWithEventStatus(eventId).users
            }.catch {
                throw it
            }
    }

    fun getFollowerWithEventState(eventId: Int): Flow<List<UserStatus>> {
        return flowOf(true)
            .map {
                eventStoryApi.getFollowerWithEventStatus(eventId).users
            }.catch {
                throw it
            }
    }
}