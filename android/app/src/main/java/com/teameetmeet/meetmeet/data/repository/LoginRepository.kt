package com.teameetmeet.meetmeet.data.repository

import com.teameetmeet.meetmeet.data.FirstSignIn
import com.teameetmeet.meetmeet.data.local.datastore.DataStoreHelper
import com.teameetmeet.meetmeet.data.network.api.LoginApi
import com.teameetmeet.meetmeet.data.network.entity.AutoLoginRequest
import com.teameetmeet.meetmeet.data.network.entity.EmailDuplicationCheckRequest
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
            }.catch {
                when (it) {
                    is FirstSignIn -> {
                        storeAppToken(it.accessToken, it.responseToken)
                    }
                }
                throw it
                //TODO("추가 예외처리 필요")
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
                // Todo 예외처리 추가
            }
    }


    fun autoLoginApp(token: String): Flow<Unit> {
        return flowOf(true)
            .map {
                val response = loginApi.autoLoginApp(AutoLoginRequest(token))
                storeAppToken(response.accessToken, response.refreshToken)
            }.catch {
                throw it
                //TODO("추가 예외처리 필요")
            }
    }

    fun checkEmailDuplication(email: String): Flow<Unit> {
        return flowOf(true)
            .map {
                val request = EmailDuplicationCheckRequest(email)
                val response = loginApi.checkEmailDuplication(request)
            }.catch {
                throw it
                //Todo 추가 예외 처리 필요
            }
    }

    fun signUp(email: String, password: String): Flow<Unit> {
        return flowOf(true)
            .map {
                val request = SelfSignRequest(email, password)
                val response = loginApi.signUp(request)
                storeAppToken(response.accessToken, response.refreshToken)
            }.catch {
                throw it
                //Todo 추가 예외 처리 필요
            }
    }

    private suspend fun storeAppToken(accessToken: String, refreshToken: String) {
        dataStore.storeAppToken(accessToken, refreshToken)
    }

}