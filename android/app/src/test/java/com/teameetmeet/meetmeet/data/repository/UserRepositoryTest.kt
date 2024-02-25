package com.teameetmeet.meetmeet.data.repository

import com.teameetmeet.meetmeet.data.local.datastore.DataStoreHelper
import com.teameetmeet.meetmeet.data.model.UserProfile
import com.teameetmeet.meetmeet.data.model.UserStatus
import com.teameetmeet.meetmeet.data.model.UsersResponse
import com.teameetmeet.meetmeet.data.network.api.UserApi
import com.teameetmeet.meetmeet.data.network.entity.AvailableResponse
import com.teameetmeet.meetmeet.data.network.entity.EventInvitationNotification
import com.teameetmeet.meetmeet.data.network.entity.EventInvitationNotificationResponse
import com.teameetmeet.meetmeet.data.network.entity.FollowNotification
import com.teameetmeet.meetmeet.data.network.entity.FollowNotificationResponse
import com.teameetmeet.meetmeet.data.network.entity.NicknameChangeRequest
import com.teameetmeet.meetmeet.data.network.entity.PasswordChangeRequest
import com.teameetmeet.meetmeet.data.network.entity.TokenRequest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class UserRepositoryTest {

    private lateinit var userRepository: UserRepository
    private val userApi: UserApi = mock()
    private val dataStore: DataStoreHelper = mock()

    @Before
    fun setUp() {
        userRepository = UserRepository(userApi, dataStore)
    }

    @Test
    fun getUserProfile() = runTest {
        val expectedUserProfile =
            UserProfile(profileImage = "image.url", nickname = "user1", email = "user1@naver.com")
        Mockito.`when`(userApi.getUserProfile()).thenReturn(expectedUserProfile)

        val resultFlow = userRepository.getUserProfile()

        resultFlow.collect { userProfile ->
            assertEquals(expectedUserProfile, userProfile)
        }
    }

    @Test
    fun getToken() = runTest {
        val expectedToken = "91732c51ed1b0f70f70ebfb3d3bab5c4"
        Mockito.`when`(dataStore.getAppToken()).thenReturn(flow { emit(expectedToken) })

        val resultFlow = userRepository.getToken()

        resultFlow.collect { token ->
            assertEquals(expectedToken, token)
        }
    }

    @Test
    fun logout() = runTest {
        Mockito.`when`(userApi.logout()).thenReturn(Unit)

        val resultFlow = userRepository.logout()

        var isCompleted = false
        resultFlow.collect { isCompleted = true }

        verify(userApi).logout()
        assertTrue(isCompleted)
    }

    @Test
    fun getLocalUserProfile() = runTest {
        val expectedUserProfile =
            UserProfile(profileImage = "image.url", nickname = "user1", email = "user1@naver.com")
        Mockito.`when`(dataStore.getUserProfile()).thenReturn(flow { emit(expectedUserProfile) })

        val resultFlow = userRepository.getLocalUserProfile()

        resultFlow.collect { userProfile ->
            assertEquals(expectedUserProfile, userProfile)
        }
    }

    @Test
    fun getUserWithFollowStatus() = runTest {
        val expectedUserWithFollowStatus = listOf(
            UserStatus(
                id = 1,
                nickname = "user1",
                profile = "image1.url",
                isFollowed = false,
                isJoined = "Joinable",
                isMe = true
            ), UserStatus(
                id = 2,
                nickname = "user2",
                profile = "image2.url",
                isFollowed = true,
                isJoined = "Joinable",
                isMe = false
            )
        )

        Mockito.`when`(dataStore.getUserProfile()).thenReturn(flow {
            emit(
                UserProfile(
                    profileImage = "image1.url", nickname = "user1", email = "user1@naver.com"
                )
            )
        })

        Mockito.`when`(userApi.getUserWithFollowStatus("user1")).thenReturn(
            UsersResponse(
                users = listOf(
                    UserStatus(
                        id = 1,
                        nickname = "user1",
                        profile = "image1.url",
                        isFollowed = false,
                        isJoined = "Joinable",
                    ), UserStatus(
                        id = 2,
                        nickname = "user2",
                        profile = "image2.url",
                        isFollowed = true,
                        isJoined = "Joinable",
                    )
                )
            )
        )

        val resultFlow = userRepository.getUserWithFollowStatus("user1")

        resultFlow.collect { userWithFollowStatus ->
            assertEquals(expectedUserWithFollowStatus, userWithFollowStatus)
        }
    }

    @Test
    fun resetDataStore() = runTest {
        Mockito.`when`(dataStore.deleteUserProfile()).thenReturn(Unit)
        Mockito.`when`(dataStore.deleteAppToken()).thenReturn(Unit)

        val resultFlow = userRepository.resetDataStore()

        var isCompleted = false
        resultFlow.collect { isCompleted = true }

        verify(dataStore).deleteUserProfile()
        verify(dataStore).deleteAppToken()
        assertTrue(isCompleted)
    }

    @Test
    fun deleteUser() = runTest {
        Mockito.`when`(userApi.deleteUser()).thenReturn(Unit)
        Mockito.`when`(dataStore.deleteUserProfile()).thenReturn(Unit)
        Mockito.`when`(dataStore.deleteAppToken()).thenReturn(Unit)

        val resultFlow = userRepository.deleteUser()

        var isCompleted = false
        resultFlow.collect { isCompleted = true }

        verify(userApi).deleteUser()
        verify(dataStore).deleteUserProfile()
        verify(dataStore).deleteAppToken()
        assertTrue(isCompleted)
    }

    @Test
    fun checkNickNameDuplication() = runTest {
        Mockito.`when`(userApi.checkNickNameDuplication("user1"))
            .thenReturn(AvailableResponse(true))
        Mockito.`when`(userApi.checkNickNameDuplication("user2"))
            .thenReturn(AvailableResponse(false))

        val resultTrueFlow = userRepository.checkNickNameDuplication("user1")
        val resultFalseFlow = userRepository.checkNickNameDuplication("user2")

        resultTrueFlow.collect { result ->
            assertTrue(result)
        }
        resultFalseFlow.collect { result ->
            assertFalse(result)
        }
    }

    @Test
    fun patchPassword() = runTest {
        val expectedUserProfile =
            UserProfile(profileImage = "image.url", nickname = "user1", email = "user1@naver.com")
        val changeRequest = PasswordChangeRequest(password = "qwer1234!")

        Mockito.`when`(userApi.patchPassword(changeRequest)).thenReturn(expectedUserProfile)

        val resultFlow = userRepository.patchPassword("qwer1234!")

        resultFlow.collect { userProfile ->
            assertEquals(expectedUserProfile, userProfile)
        }
    }

    @Test
    fun patchNickname() = runTest {

        val changeRequest = NicknameChangeRequest(nickname = "user3333")

        Mockito.`when`(userApi.patchNickname(changeRequest)).thenReturn(Unit)

        val resultFlow = userRepository.patchNickname("user3333")

        var isCompleted = false
        resultFlow.collect { isCompleted = true }

        assertTrue(isCompleted)
    }

    @Test
    fun patchProfileImage() = runTest {}

    @Test
    fun updateFcmToken() = runTest {
        val token = "91732c51ed1b0f70f70ebfb3d3bab5c4"

        Mockito.`when`(userApi.updateFcmToken(TokenRequest(token))).thenReturn(Unit)

        userRepository.updateFcmToken("91732c51ed1b0f70f70ebfb3d3bab5c4")

        verify(userApi).updateFcmToken(TokenRequest(token))
    }

    @Test
    fun getFollowNotification() = runTest {
        val expectedFollowNotification = listOf(
            FollowNotification(
                inviteId = 1, id = 1, nickname = "user1", profile = "image.url", status = "status"
            ), FollowNotification(
                inviteId = 2, id = 2, nickname = "user2", profile = "image.url", status = "status"
            ), FollowNotification(
                inviteId = 3, id = 3, nickname = "user3", profile = "image.url", status = "status"
            )
        )
        Mockito.`when`(userApi.getFollowNotification()).thenReturn(expectedFollowNotification.map {
            FollowNotificationResponse(
                type = "follow", body = it
            )
        })

        val resultFlow = userRepository.getFollowNotification()

        resultFlow.collect { followNotification ->
            assertEquals(expectedFollowNotification, followNotification)
        }
    }

    @Test
    fun getEventInvitationNotification() = runTest {
        val expectedEventInvitationNotification = listOf(
            EventInvitationNotification(
                inviteId = 1,
                eventId = 1,
                title = "title1",
                startDate = "START_DATE",
                endDate = "END_DATE",
                nickname = "user1",
                profile = "image.url",
                status = "status"
            ), EventInvitationNotification(
                inviteId = 2,
                eventId = 2,
                title = "title1",
                startDate = "START_DATE",
                endDate = "END_DATE",
                nickname = "user2",
                profile = "image.url",
                status = "status"
            ), EventInvitationNotification(
                inviteId = 3,
                eventId = 3,
                title = "title1",
                startDate = "START_DATE",
                endDate = "END_DATE",
                nickname = "user3",
                profile = "image.url",
                status = "status"
            )
        )
        Mockito.`when`(userApi.getEventInvitationNotification())
            .thenReturn(expectedEventInvitationNotification.map {
                EventInvitationNotificationResponse(
                    type = "event", body = it
                )
            })

        val resultFlow = userRepository.getEventInvitationNotification()

        resultFlow.collect { eventInvitationNotification ->
            assertEquals(expectedEventInvitationNotification, eventInvitationNotification)
        }
    }

    @Test
    fun deleteUserNotification() = runTest {
        val ids = "1 2 3 4 5"

        Mockito.`when`(userApi.deleteUserNotification(ids)).thenReturn(Unit)

        val resultFlow = userRepository.deleteUserNotification(ids)

        var isCompleted = false
        resultFlow.collect { isCompleted = true }

        verify(userApi).deleteUserNotification(ids)
        assertTrue(isCompleted)
    }
}