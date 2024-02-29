package com.teameetmeet.meetmeet.data.repository

import com.teameetmeet.meetmeet.MeetMeetApp
import com.teameetmeet.meetmeet.data.datasource.LocalCalendarDataSource
import com.teameetmeet.meetmeet.data.datasource.RemoteCalendarDataSource
import com.teameetmeet.meetmeet.data.local.database.entity.Event
import com.teameetmeet.meetmeet.data.network.entity.EventResponse
import com.teameetmeet.meetmeet.data.network.entity.UserEventResponse
import com.teameetmeet.meetmeet.data.toEvent
import com.teameetmeet.meetmeet.presentation.model.EventColor
import com.teameetmeet.meetmeet.presentation.model.EventNotification
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CalendarRepositoryTest {
    private lateinit var calendarRepository: CalendarRepository
    private val mockLocalDataSource: LocalCalendarDataSource = mockk()
    private val mockRemoteDataSource: RemoteCalendarDataSource = mockk()
    private val mockApp: MeetMeetApp = mockk()


    @Before
    fun setUp() {
        calendarRepository = CalendarRepository(mockLocalDataSource, mockRemoteDataSource)
        mockkObject(MeetMeetApp.Companion)
        every { MeetMeetApp.instance } returns mockApp
        every { mockApp.getString(any()) } returns "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
    }

    @After
    fun tearDown() {
        clearMocks(mockLocalDataSource, mockRemoteDataSource, mockApp)
    }

    @Test
    fun getSyncedEventTest() = runTest {
        val expectedResponse = flowOf(
            listOf(
                EventResponse(
                    id = 1,
                    title = "title",
                    startDate = "2024-01-19T20:34:46.177+09:00",
                    endDate = "2024-01-19T20:34:46.177+09:00",
                    eventMembers = listOf(),
                    authority = "OWNER",
                    repeatPolicyId = 1,
                    isJoinable = true,
                )
            )
        )

        val expectedEvent = flowOf(
            listOf(
                Event(
                    id = 1,
                    title = "title",
                    startDateTime = 1708967388L,
                    endDateTime = 1708967388L
                )
            )
        )

        every { mockRemoteDataSource.getEvents(any(), any()) } returns expectedResponse
        coEvery { mockLocalDataSource.deleteEvents(any(), any()) } returns Unit
        every { mockLocalDataSource.getEvents(any(), any()) } returns expectedEvent

        val result = calendarRepository.getSyncedEvents(0, 0)

        assertEquals(expectedEvent.toList(), result.toList())
    }

    @Test
    fun getEventsByUserIdTest() = runTest {
        val expectedResponse = flowOf(
            listOf(
                UserEventResponse(
                    id = 1,
                    title = "title",
                    startDate = "2024-01-19T20:00:00.000+09:00",
                    endDate = "2024-01-19T20:00:00.000+09:00"
                )
            )
        )

        val expectedEvent = expectedResponse.map { it.map(UserEventResponse::toEvent) }

        every {
            mockRemoteDataSource.getEventsByUserId(
                any(), any(), any()
            )
        } returns expectedResponse

        val result = calendarRepository.getEventsByUserId(1, 0, 0)

        assertEquals(expectedEvent.toList(), result.toList())
    }

    @Test
    fun deleteEvents() = runTest {
        coEvery { mockLocalDataSource.deleteAll() } returns Unit

        calendarRepository.deleteEvents()

        coVerify { mockLocalDataSource.deleteAll() }
    }

    @Test
    fun addEventTest() = runTest {
        val expectedResponse = flowOf(
            listOf(
                EventResponse(
                    id = 1,
                    title = "title",
                    startDate = "2024-01-19T20:00:00.000+09:00",
                    endDate = "2024-01-19T20:00:00.000+09:00",
                    eventMembers = listOf(),
                    authority = "OWNER",
                    repeatPolicyId = 1,
                    isJoinable = true
                )
            )
        )

        every { mockRemoteDataSource.addEvent(any()) } returns expectedResponse

        val result = calendarRepository.addEvent(
            title = "title",
            startDate = "2024-01-19T20:00:00.000+09:00",
            endDate = "2024-01-19T20:00:00.000+09:00",
            isJoinable = true,
            isVisible = true,
            memo = "memo",
            repeatTerm = null,
            repeatFrequency = 1,
            repeatEndDate = "2024-01-19",
            color = EventColor.RED,
            alarm = EventNotification.NONE
        )

        assertEquals(expectedResponse.toList(), result.toList())
    }

    @Test
    fun searchEventsTest() = runTest {
        val expectedResponse = flowOf(
            listOf(
                EventResponse(
                    id = 1,
                    title = "title",
                    startDate = "2024-01-19T20:00:00.000+09:00",
                    endDate = "2024-01-19T20:00:00.000+09:00",
                    eventMembers = listOf(),
                    authority = "OWNER",
                    repeatPolicyId = 1,
                    isJoinable = true
                )
            )
        )

        every { mockRemoteDataSource.searchEvents(any(), any(), any()) } returns expectedResponse

        val result = calendarRepository.searchEvents(
            keyword = "keyword",
            startDate = "2024-01-19T20:00:00.000+09:00",
            endDate = "2024-01-19T20:00:00.000+09:00",
        )

        assertEquals(expectedResponse.toList(), result.toList())
    }
}