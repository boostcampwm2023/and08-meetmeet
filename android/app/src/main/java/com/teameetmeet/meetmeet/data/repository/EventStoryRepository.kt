package com.teameetmeet.meetmeet.data.repository

import android.net.Uri
import com.teameetmeet.meetmeet.data.local.datastore.DataStoreHelper
import com.teameetmeet.meetmeet.data.model.EventDetail
import com.teameetmeet.meetmeet.data.model.EventStory
import com.teameetmeet.meetmeet.data.model.FeedDetail
import com.teameetmeet.meetmeet.data.model.UserStatus
import com.teameetmeet.meetmeet.data.network.api.EventStoryApi
import com.teameetmeet.meetmeet.data.network.entity.AddEventRequest
import com.teameetmeet.meetmeet.data.network.entity.AddFeedCommentRequest
import com.teameetmeet.meetmeet.data.network.entity.AnnouncementRequest
import com.teameetmeet.meetmeet.data.network.entity.EventInviteAcceptRequest
import com.teameetmeet.meetmeet.data.network.entity.EventInviteRequest
import com.teameetmeet.meetmeet.data.network.entity.JoinRequest
import com.teameetmeet.meetmeet.data.toException
import com.teameetmeet.meetmeet.presentation.model.EventColor
import com.teameetmeet.meetmeet.presentation.model.EventNotification
import com.teameetmeet.meetmeet.util.getMimeType
import com.teameetmeet.meetmeet.util.toAbsolutePath
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class EventStoryRepository @Inject constructor(
    private val eventStoryApi: EventStoryApi,
    private val dataStore: DataStoreHelper
) {

    fun getEventStory(id: Int): Flow<EventStory> {
        return flowOf(true)
            .map {
                eventStoryApi.getStory(id)
            }.catch {
                throw it.toException()
            }
    }

    fun getEventStoryDetail(id: Int): Flow<EventDetail> {
        return flowOf(Unit)
            .map {
                eventStoryApi.getStoryDetail(id).result
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

    fun editAnnouncement(eventId: Int, message: String?): Flow<Unit> {
        return flowOf(true)
            .map {
                eventStoryApi.editAnnouncement(eventId, AnnouncementRequest(message))
            }.catch {
                throw it.toException()
            }
    }

    fun createFeed(eventId: Int, memo: String?, media: List<Uri>?): Flow<Unit> {
        return flowOf(true).map {
            val contents = media?.map { uri ->
                val file = uri.toAbsolutePath()?.let { File(it) } ?: return@map null
                val type = uri.getMimeType() ?: return@map null
                MultipartBody.Part.createFormData(
                    "contents", file.name, file.asRequestBody(type.toMediaType())
                )
            }?.filterNotNull()
            eventStoryApi.createFeed(
                eventId.toString().toRequestBody(),
                memo?.toRequestBody(),
                contents
            )
        }.catch {
            throw it.toException()
        }
    }

    fun joinEventStory(eventId: Int): Flow<Unit> {
        return flowOf(true).map {
            eventStoryApi.joinEventStory(JoinRequest(eventId))
        }.catch {
            throw it.toException()
        }
    }

    fun getFeedDetail(feedId: Int): Flow<FeedDetail> {
        return flowOf(true)
            .map {
                val userNickname = dataStore.getUserProfile().first().nickname
                eventStoryApi.getFeedDetail(feedId).let { feed ->
                    feed.copy(
                        isMine = userNickname == feed.author.nickname,
                        comments = feed.comments.map { comment ->
                            comment.copy(isMine = comment.author.nickname == userNickname)
                        }
                    )
                }
            }.catch {
                throw it.toException()
            }
    }

    fun deleteFeed(feedId: Int): Flow<Unit> {
        return flowOf(true)
            .map {
                eventStoryApi.deleteFeed(feedId)
            }.catch {
                throw it.toException()
            }
    }

    fun addFeedComment(feedId: Int, memo: String): Flow<Unit> {
        return flowOf(true)
            .map {
                eventStoryApi.addFeedComment(
                    feedId, AddFeedCommentRequest(memo)
                )
            }.catch {
                throw it.toException()
            }
    }

    fun deleteFeedComment(feedId: Int, commentId: Int): Flow<Unit> {
        return flowOf(true)
            .map {
                eventStoryApi.deleteFeedComment(feedId, commentId)
            }.catch {
                throw it.toException()
            }
    }

    fun inviteEvent(eventId: Int, userId: Int): Flow<Unit> {
        return flowOf(true)
            .map {
                val request = EventInviteRequest(userId = userId, eventId = eventId)
                eventStoryApi.inviteEvent(request)
            }.catch {
                throw it.toException()
            }
    }

    fun getFollowingWithEventState(eventId: Int): Flow<List<UserStatus>> {
        return flowOf(true)
            .map {
                eventStoryApi.getFollowingWithEventStatus(eventId).users
            }.catch {
                throw it.toException()
            }
    }

    fun getFollowerWithEventState(eventId: Int): Flow<List<UserStatus>> {
        return flowOf(true)
            .map {
                eventStoryApi.getFollowerWithEventStatus(eventId).users
            }.catch {
                throw it.toException()
            }
    }

    fun getUserWithEventStatus(eventId: Int, nickname: String): Flow<List<UserStatus>> {
        return flowOf(true)
            .map {
                val userNickname = dataStore.getUserProfile().first().nickname
                eventStoryApi.getUserWithEventStatus(eventId, nickname).users.map {
                    if (it.nickname == userNickname) {
                        it.copy(isMe = true)
                    } else {
                        it
                    }
                }
            }.catch {
                throw it.toException()
            }
    }

    fun acceptEventInvite(accept: Boolean, inviteId: Int, eventId: Int): Flow<Unit> {
        return flowOf(true)
            .map {
                eventStoryApi.acceptInviteEvent(accept, EventInviteAcceptRequest(inviteId, eventId))
            }.catch {
                throw it.toException()
            }
    }
}