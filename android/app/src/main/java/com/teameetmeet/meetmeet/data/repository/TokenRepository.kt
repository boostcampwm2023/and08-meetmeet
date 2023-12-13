package com.teameetmeet.meetmeet.data.repository

import com.teameetmeet.meetmeet.data.ExpiredRefreshTokenException
import com.teameetmeet.meetmeet.data.NoDataException
import com.teameetmeet.meetmeet.data.local.datastore.DataStoreHelper
import com.teameetmeet.meetmeet.data.network.api.AuthApi
import com.teameetmeet.meetmeet.data.network.entity.TokenRequest
import com.teameetmeet.meetmeet.data.network.entity.RefreshAccessTokenRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import javax.inject.Inject

class TokenRepository @Inject constructor(
    private val dataStoreHelper: DataStoreHelper,
    private val authApi: AuthApi
) {
    suspend fun refreshAccessToken(): String {
        val token = dataStoreHelper.getRefreshToken().first() ?: throw NoDataException()
        try {
            val response = authApi.refreshAccessToken(RefreshAccessTokenRequest(token))
            dataStoreHelper.storeAppToken(response.accessToken, response.refreshToken)
            return response.accessToken
        } catch (e: Exception) {
            throw ExpiredRefreshTokenException()
        }
    }

    fun autoLoginApp(token: String): Flow<Unit> {
        return flowOf(true)
            .map {
                authApi.checkValidAccessToken(TokenRequest(token))
            }.catch {
                when(it) {
                    is HttpException -> {
                        if(it.code() == 418) {
                            val token = refreshAccessToken()
                            emit(autoLoginApp(token).first())
                        } else {
                            throw it
                        }
                    }
                    else -> throw it
                }
            }
    }

    suspend fun getAccessToken(): String {
        return dataStoreHelper.getAppToken().first() ?: throw NoDataException()
    }

}