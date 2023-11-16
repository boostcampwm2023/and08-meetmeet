package com.teameetmeet.meetmeet.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.teameetmeet.meetmeet.data.network.api.LoginApi
import com.teameetmeet.meetmeet.data.network.entity.KakaoLoginRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val loginApi: LoginApi,
    private val dataStore: DataStore<Preferences>
) {
    fun loginKakao(id: Long): Flow<Unit> {
        return flowOf(true)
            .map {
                val response = loginApi.loginKakao(kakaoLoginRequest = KakaoLoginRequest(id.toString()))
                dataStore.edit{
                    it[ACCESS_TOKEN] = response.accessToken
                    it[REFRESH_TOKEN] = response.refreshToken
                }
                Unit
            }.catch {
                throw it
                //추가 예외처리 필요
            }
    }

    suspend fun fetchKakaoToken(accessToken: String, refreshToken: String)  {
        dataStore.edit {
            it[KAKAO_ACCESS_TOKEN] = accessToken
            it[KAKAO_REFRESH_TOKEN] = refreshToken
        }
    }

    companion object {
        val ACCESS_TOKEN = stringPreferencesKey("accessToken")
        val REFRESH_TOKEN = stringPreferencesKey("refreshToken")
        val KAKAO_ACCESS_TOKEN = stringPreferencesKey("kakao_AccessToken")
        val KAKAO_REFRESH_TOKEN = stringPreferencesKey("kakao_RefreshToken")
    }
}