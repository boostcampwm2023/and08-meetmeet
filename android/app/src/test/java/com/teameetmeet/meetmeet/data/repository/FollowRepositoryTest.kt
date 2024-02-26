package com.teameetmeet.meetmeet.data.repository

import com.teameetmeet.meetmeet.data.model.UserStatus
import com.teameetmeet.meetmeet.data.model.UsersResponse
import com.teameetmeet.meetmeet.data.network.api.FollowApi
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class FollowRepositoryTest {
    private lateinit var followRepository: FollowRepository
    private val mockFollowApi: FollowApi = mockk()

    @Before
    fun setUp() {
        followRepository = FollowRepository(mockFollowApi)
    }

    @After
    fun tearDown() {
        clearMocks(mockFollowApi)
    }

    @Test
    fun followTest() = runTest {
        coEvery { mockFollowApi.follow(any()) } returns Unit

        followRepository.follow(1).first()

        coVerify(exactly = 1) { mockFollowApi.follow(any()) }
    }

    @Test
    fun unfollowTest() = runTest {
        coEvery { mockFollowApi.unFollow(any()) } returns Unit

        followRepository.unFollow(1).first()

        coVerify(exactly = 1) { mockFollowApi.unFollow(any()) }
    }

    @Test
    fun getFollowingWithFollowStateTest() = runTest {
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

        coEvery { mockFollowApi.getFollowingWithFollowStatus() } returns expectedResponse

        val result = followRepository.getFollowingWithFollowState()

        assertEquals(expectedResult.toList(), result.toList())
    }

    @Test
    fun getFollowerWithFollowStateTest() = runTest {
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

        coEvery { mockFollowApi.getFollowerWithFollowStatus() } returns expectedResponse

        val result = followRepository.getFollowerWithFollowState()

        assertEquals(expectedResult.toList(), result.toList())
    }
}