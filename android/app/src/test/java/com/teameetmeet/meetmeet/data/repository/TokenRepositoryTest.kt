package com.teameetmeet.meetmeet.data.repository

import com.teameetmeet.meetmeet.data.local.datastore.DataStoreHelper
import com.teameetmeet.meetmeet.data.network.api.AuthApi
import com.teameetmeet.meetmeet.data.network.entity.LoginResponse
import com.teameetmeet.meetmeet.data.network.entity.RefreshAccessTokenRequest
import com.teameetmeet.meetmeet.data.network.entity.TokenRequest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock

class TokenRepositoryTest {

    private lateinit var tokenRepository: TokenRepository
    private val authApi: AuthApi = mock()
    private val dataStoreHelper: DataStoreHelper = mock()

    @Before
    fun setUp() {
        tokenRepository = TokenRepository(dataStoreHelper, authApi)
    }

    @Test
    fun refreshAccessToken() = runTest {
        val expectedAccessToken = "accessToken"
        val refreshToken = "refreshToken"
        Mockito.`when`(dataStoreHelper.getRefreshToken()).thenReturn(flow { emit(refreshToken) })
        Mockito.`when`(authApi.refreshAccessToken(RefreshAccessTokenRequest(refreshToken)))
            .thenReturn(
                LoginResponse(
                    accessToken = expectedAccessToken,
                    refreshToken = refreshToken,
                    isNewUser = false
                )
            )
        Mockito.`when`(dataStoreHelper.storeAppToken("accessToken", "refreshToken"))
            .thenReturn(Unit)

        val accessToken = tokenRepository.refreshAccessToken()

        assertEquals(expectedAccessToken, accessToken)
    }

    @Test
    fun autoLoginApp() = runTest {
        val accessToken = "accessToken"
        Mockito.`when`(authApi.checkValidAccessToken(TokenRequest(accessToken))).thenReturn(Unit)

        val resultFlow = tokenRepository.autoLoginApp(accessToken)

        var isCompleted = false
        resultFlow.collect { isCompleted = true }

        Mockito.verify(authApi).checkValidAccessToken(TokenRequest(accessToken))
        assertTrue(isCompleted)
    }

    @Test
    fun getAccessToken() = runTest {
        val expectedAccessToken = "accessToken"
        Mockito.`when`(dataStoreHelper.getAppToken()).thenReturn(flow { emit(expectedAccessToken) })

        val accessToken = tokenRepository.getAccessToken()

        assertEquals(expectedAccessToken, accessToken)
    }
}