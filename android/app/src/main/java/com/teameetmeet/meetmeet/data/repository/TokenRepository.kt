package com.teameetmeet.meetmeet.data.repository

import com.teameetmeet.meetmeet.data.ExpiredRefreshTokenException
import com.teameetmeet.meetmeet.data.NoDataException
import com.teameetmeet.meetmeet.data.local.datastore.DataStoreHelper
import com.teameetmeet.meetmeet.data.network.api.AuthApi
import com.teameetmeet.meetmeet.data.network.entity.RefreshAccessTokenRequest
import kotlinx.coroutines.flow.first
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
            println(e)
            throw ExpiredRefreshTokenException()
        }
    }

    suspend fun getAccessToken(): String {
        return dataStoreHelper.getAppToken().first() ?: throw NoDataException()
    }

}