package com.teameetmeet.meetmeet.data.repository

import com.teameetmeet.meetmeet.data.local.datastore.DataStoreHelper
import com.teameetmeet.meetmeet.data.model.EventDetail
import com.teameetmeet.meetmeet.data.model.EventMember
import com.teameetmeet.meetmeet.data.model.EventStory
import com.teameetmeet.meetmeet.data.model.FeedDetail
import com.teameetmeet.meetmeet.data.model.UserProfile
import com.teameetmeet.meetmeet.data.model.UserStatus
import com.teameetmeet.meetmeet.data.model.UsersResponse
import com.teameetmeet.meetmeet.data.network.api.EventStoryApi
import com.teameetmeet.meetmeet.data.network.entity.EventStoryDetailResponse
import com.teameetmeet.meetmeet.presentation.model.EventColor
import com.teameetmeet.meetmeet.presentation.model.EventNotification
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class EventStoryRepositoryTest {
    private lateinit var eventStoryRepository: EventStoryRepository
    private val mockEventStoryApi: EventStoryApi = mockk()
    private val mockDataStore: DataStoreHelper = mockk()

    @Before
    fun setUp() {
        eventStoryRepository = EventStoryRepository(mockEventStoryApi, mockDataStore)
    }

    @After
    fun tearDown() {
        clearMocks(mockEventStoryApi, mockDataStore)
    }

    @Test
    fun getEventStoryTest() = runTest {
        val expectedStory = EventStory(
            id = 1,
            title = "title",
            startDate = "2024-01-19T20:34:46.177+09:00",
            endDate = "2024-01-19T20:34:46.177+09:00",
            eventMembers = listOf(),
            announcement = null,
            authority = null,
            repeatPolicyId = null,
            isJoin = true,
            feeds = listOf()
        )

        val expectedResult = flowOf(expectedStory)

        coEvery { mockEventStoryApi.getStory(any()) } returns expectedStory

        val result = eventStoryRepository.getEventStory(1)

        assertEquals(expectedResult.toList(), result.toList())
    }

    @Test
    fun getEventStoryDetailTest() = runTest {
        val expectedResponse = EventStoryDetailResponse(
            EventDetail(
                id = 1,
                title = "title",
                startDate = "2024-01-19T20:34:46.177+09:00",
                endDate = "2024-01-19T20:34:46.177+09:00",
                authority = null,
                isJoin = true,
                isVisible = true,
                memo = null,
                color = -39579,
                alarmMinutes = -1,
                repeatTerm = null,
                repeatFrequency = null,
                repeatEndDate = null
            )
        )

        val expectedResult = flowOf(expectedResponse.result)

        coEvery { mockEventStoryApi.getStoryDetail(any()) } returns expectedResponse

        val result = eventStoryRepository.getEventStoryDetail(1)

        assertEquals(expectedResult.toList(), result.toList())
    }

    @Test
    fun deleteEventStoryTest() = runTest {
        coEvery { mockEventStoryApi.deleteEventStory(any(), any()) } returns Unit

        eventStoryRepository.deleteEventStory(1).first()

        coVerify(exactly = 1) { mockEventStoryApi.deleteEventStory(any(), any()) }
    }

    @Test
    fun editEventStoryTest() = runTest {
        coEvery { mockEventStoryApi.editEventStory(any(), any(), any()) } returns Unit

        eventStoryRepository.editEventStory(
            eventId = 1,
            isAll = true,
            title = "title",
            startDate = "2024-01-19T20:34:46.177+09:00",
            endDate = "2024-01-19T20:34:46.177+09:00",
            isJoinable = true,
            isVisible = true,
            memo = "memo",
            repeatTerm = null,
            repeatFrequency = null,
            repeatEndDate = null,
            color = EventColor.RED,
            alarm = EventNotification.NONE
        ).first()

        coVerify(exactly = 1) { mockEventStoryApi.editEventStory(any(), any(), any()) }
    }

    @Test
    fun editAnnouncementTest() = runTest {
        coEvery { mockEventStoryApi.editAnnouncement(any(), any()) } returns Unit

        eventStoryRepository.editAnnouncement(
            eventId = 1,
            message = null
        ).first()

        coVerify(exactly = 1) { mockEventStoryApi.editAnnouncement(any(), any()) }
    }

    @Test
    fun createFeedTest() = runTest {
        coEvery { mockEventStoryApi.createFeed(any(), any(), any()) } returns Unit

        eventStoryRepository.createFeed(
            eventId = 1,
            memo = null,
            media = listOf()
        ).first()

        coVerify(exactly = 1) { mockEventStoryApi.createFeed(any(), any(), any()) }
    }

    @Test
    fun joinEventStory() = runTest {
        coEvery { mockEventStoryApi.joinEventStory(any()) } returns Unit

        eventStoryRepository.joinEventStory(1).first()

        coVerify(exactly = 1) { mockEventStoryApi.joinEventStory(any()) }
    }

    @Test
    fun getFeedDetail() = runTest {
        val expectedUserProfile = flowOf(
            UserProfile(
                profileImage = "image.url",
                nickname = "user1",
                email = "user1@naver.com"
            )
        )

        val expectedFeedDetail = FeedDetail(
            id = 1,
            memo = null,
            author = EventMember(id = 1, nickname = "user1", profile = "image.url"),
            contents = listOf(),
            comments = listOf(),
            isMine = true
        )

        val expectedResult = flowOf(expectedFeedDetail)

        every { mockDataStore.getUserProfile() } returns expectedUserProfile
        coEvery { mockEventStoryApi.getFeedDetail(any()) } returns expectedFeedDetail

        val result = eventStoryRepository.getFeedDetail(1)

        assertEquals(expectedResult.toList(), result.toList())
    }

    @Test
    fun deleteFeedTest() = runTest {
        coEvery { mockEventStoryApi.deleteFeed(any()) } returns Unit

        eventStoryRepository.deleteFeed(1).first()

        coVerify(exactly = 1) { mockEventStoryApi.deleteFeed(any()) }
    }

    @Test
    fun addFeedCommentTest() = runTest {
        coEvery { mockEventStoryApi.addFeedComment(any(), any()) } returns Unit

        eventStoryRepository.addFeedComment(1, "comment").first()

        coVerify(exactly = 1) { mockEventStoryApi.addFeedComment(any(), any()) }
    }

    @Test
    fun deleteFeedCommentTest() = runTest {
        coEvery { mockEventStoryApi.deleteFeedComment(any(), any()) } returns Unit

        eventStoryRepository.deleteFeedComment(1, 1).first()

        coVerify(exactly = 1) { mockEventStoryApi.deleteFeedComment(any(), any()) }
    }

    @Test
    fun inviteEventTest() = runTest {
        coEvery { mockEventStoryApi.inviteEvent(any()) } returns Unit

        eventStoryRepository.inviteEvent(1, 1).first()

        coVerify(exactly = 1) { mockEventStoryApi.inviteEvent(any()) }
    }

    @Test
    fun getFollowingWithEventStateTest() = runTest {
        val expectedResponse = UsersResponse(
            listOf(
                UserStatus(
                    id = 1,
                    nickname = "user1",
                    profile = "image.url"
                )
            )
        )

        val expectedResult = flowOf(expectedResponse.users)

        coEvery { mockEventStoryApi.getFollowingWithEventStatus(any()) } returns expectedResponse

        val result = eventStoryRepository.getFollowingWithEventState(1)

        assertEquals(expectedResult.toList(), result.toList())
    }

    @Test
    fun getFollowerWithEventStateTest() = runTest {
        val expectedResponse = UsersResponse(
            listOf(
                UserStatus(
                    id = 1,
                    nickname = "user1",
                    profile = "image.url"
                )
            )
        )

        val expectedResult = flowOf(expectedResponse.users)

        coEvery { mockEventStoryApi.getFollowerWithEventStatus(any()) } returns expectedResponse

        val result = eventStoryRepository.getFollowerWithEventState(1)

        assertEquals(expectedResult.toList(), result.toList())
    }

    @Test
    fun getUserWithEventStatusTest() = runTest {
        val expectedUserProfile = flowOf(
            UserProfile(
                profileImage = "image.url",
                nickname = "user1",
                email = "user1@naver.com"
            )
        )

        val expectedResponse = UsersResponse(
            listOf(
                UserStatus(
                    id = 1,
                    nickname = "user1",
                    profile = "image.url",
                    isMe = true
                )
            )
        )

        val expectedResult = flowOf(expectedResponse.users)

        every { mockDataStore.getUserProfile() } returns expectedUserProfile
        coEvery { mockEventStoryApi.getUserWithEventStatus(any(), any()) } returns expectedResponse

        val result = eventStoryRepository.getUserWithEventStatus(1, "user1")

        assertEquals(expectedResult.toList(), result.toList())
    }

    @Test
    fun acceptEventInviteTest() = runTest {
        coEvery { mockEventStoryApi.acceptInviteEvent(any(), any()) } returns Unit

        eventStoryRepository.acceptEventInvite(true, 1, 1).first()

        coVerify(exactly = 1) { mockEventStoryApi.acceptInviteEvent(any(), any()) }
    }
}