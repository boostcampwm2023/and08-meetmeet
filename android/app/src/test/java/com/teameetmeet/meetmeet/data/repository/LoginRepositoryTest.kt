package com.teameetmeet.meetmeet.data.repository

import com.teameetmeet.meetmeet.data.local.datastore.DataStoreHelper
import com.teameetmeet.meetmeet.data.network.api.LoginApi
import com.teameetmeet.meetmeet.data.network.entity.AvailableResponse
import com.teameetmeet.meetmeet.data.network.entity.KakaoLoginRequest
import com.teameetmeet.meetmeet.data.network.entity.LoginResponse
import com.teameetmeet.meetmeet.data.network.entity.SelfSignRequest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify


class LoginRepositoryTest {

    private lateinit var loginRepository: LoginRepository
    private val loginApi: LoginApi = mock()
    private val dataStore: DataStoreHelper = mock()

    @Before
    fun setUp() {
        loginRepository = LoginRepository(loginApi, dataStore)
    }

    @Test
    fun loginKakao() = runTest {
        val id = 1234567890L

        Mockito.`when`(loginApi.loginKakao(KakaoLoginRequest(id.toString()))).thenReturn(
            LoginResponse(
                accessToken = "accessToken",
                refreshToken = "refreshToken",
                isNewUser = false
            )
        )

        Mockito.`when`(dataStore.storeAppToken("accessToken", "refreshToken"))
            .thenReturn(Unit)

        val resultFlow = loginRepository.loginKakao(id)

        var isCompleted = false
        resultFlow.collect { isCompleted = true }

        verify(loginApi).loginKakao(KakaoLoginRequest(id.toString()))
        verify(dataStore).storeAppToken("accessToken", "refreshToken")
        assertTrue(isCompleted)
    }

    @Test
    fun loginSelf() = runTest {
        val email = "user1@naver.com"
        val password = "qwer1234!"
        Mockito.`when`(loginApi.loginSelf(SelfSignRequest(email, password))).thenReturn(
            LoginResponse(
                accessToken = "accessToken",
                refreshToken = "refreshToken",
                isNewUser = false
            )
        )
        Mockito.`when`(dataStore.storeAppToken("accessToken", "refreshToken"))
            .thenReturn(Unit)

        val resultFlow = loginRepository.loginSelf(email, password)

        var isCompleted = false
        resultFlow.collect { isCompleted = true }

        verify(loginApi).loginSelf(SelfSignRequest(email, password))
        verify(dataStore).storeAppToken("accessToken", "refreshToken")
        assertTrue(isCompleted)
    }

    @Test
    fun checkEmailDuplication() = runTest {
        Mockito.`when`(loginApi.checkEmailDuplication("user1@naver.com"))
            .thenReturn(AvailableResponse(true))
        Mockito.`when`(loginApi.checkEmailDuplication("user2@naver.com"))
            .thenReturn(AvailableResponse(false))

        val resultTrueFlow = loginRepository.checkEmailDuplication("user1@naver.com")
        val resultFalseFlow = loginRepository.checkEmailDuplication("user2@naver.com")

        resultTrueFlow.collect { result ->
            assertTrue(result)
        }
        resultFalseFlow.collect { result ->
            assertFalse(result)
        }
    }

    @Test
    fun signUp() = runTest {
        val email = "user1@naver.com"
        val password = "qwer1234!"

        Mockito.`when`(loginApi.signUp(SelfSignRequest(email, password))).thenReturn(Unit)
        Mockito.`when`(loginApi.loginSelf(SelfSignRequest(email, password))).thenReturn(
            LoginResponse(
                accessToken = "accessToken",
                refreshToken = "refreshToken",
                isNewUser = false
            )
        )
        Mockito.`when`(dataStore.storeAppToken("accessToken", "refreshToken"))
            .thenReturn(Unit)

        val resultFlow = loginRepository.signUp(email, password)

        var isCompleted = false
        resultFlow.collect { isCompleted = true }

        verify(loginApi).loginSelf(SelfSignRequest(email, password))
        verify(dataStore).storeAppToken("accessToken", "refreshToken")
        assertTrue(isCompleted)
    }
}