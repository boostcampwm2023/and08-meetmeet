package com.teameetmeet.meetmeet.data.repository

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.teameetmeet.meetmeet.data.local.datastore.DataStoreHelper
import com.teameetmeet.meetmeet.data.network.api.LoginApi
import com.teameetmeet.meetmeet.data.network.entity.AutoLoginRequest
import com.teameetmeet.meetmeet.data.network.entity.KakaoLoginRequest
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
                val response = loginApi.loginKakao(kakaoLoginRequest = KakaoLoginRequest(id.toString()))
                storeAppToken(response.accessToken, response.refreshToken)
            }.catch {
                throw it
                //TODO("추가 예외처리 필요")
            }
    }

    fun autoLoginApp(token: String) :Flow<Unit> {
        return flowOf(true)
            .map {
                val response = loginApi.autoLoginApp(AutoLoginRequest(token))
                storeAppToken(response.accessToken, response.refreshToken)
            }.catch {
                throw it
                //TODO("추가 예외처리 필요")
            }
    }

    private suspend fun storeAppToken(accessToken: String, refreshToken: String) {
        dataStore.storeAppToken(accessToken, refreshToken)
    }

}