package com.teameetmeet.meetmeet.data.repository

import android.util.Log
import com.teameetmeet.meetmeet.data.FirstSignIn
import com.teameetmeet.meetmeet.data.local.datastore.DataStoreHelper
import com.teameetmeet.meetmeet.data.network.api.LoginApi
import com.teameetmeet.meetmeet.data.network.entity.KakaoLoginRequest
import com.teameetmeet.meetmeet.data.network.entity.SelfSignRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val loginApi: LoginApi,
    private val dataStore: DataStoreHelper
) {
    fun loginKakao(id: Long): Flow<Unit> {
        return flowOf(true)
            .map {
                val response =
                    loginApi.loginKakao(kakaoLoginRequest = KakaoLoginRequest(id.toString()))
                storeAppToken(response.accessToken, response.refreshToken)
                if(response.isNewUser) {
                    throw FirstSignIn()
                }
            }.catch {
                throw it
            }
    }

    fun loginSelf(email: String, password: String): Flow<Unit> {
        return flowOf(true)
            .map {
                val request = SelfSignRequest(email, password)
                val response = loginApi.loginSelf(request)
                storeAppToken(response.accessToken, response.refreshToken)
            }.catch {
                throw it
            }
    }

    fun checkEmailDuplication(email: String): Flow<Boolean> {
        return flowOf(true)
            .map {
                val response = loginApi.checkEmailDuplication(email)
                response.isAvailable
            }.catch {
                throw it
            }
    }

    fun signUp(email: String, password: String): Flow<Unit> {
        return flowOf(true)
            .map {
                val request = SelfSignRequest(email, password)
                loginApi.signUp(request)
                val response = loginApi.loginSelf(request)
                storeAppToken(response.accessToken, response.refreshToken)
            }.catch {
                throw it
            }
    }

    private suspend fun storeAppToken(accessToken: String, refreshToken: String) {
        dataStore.storeAppToken(accessToken, refreshToken)
    }
}