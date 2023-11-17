package com.teameetmeet.meetmeet.data.repository

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.teameetmeet.meetmeet.data.NoDataException
import com.teameetmeet.meetmeet.data.local.datastore.DataStoreHelper
import com.teameetmeet.meetmeet.data.network.api.UserApi
import com.teameetmeet.meetmeet.data.network.entity.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userApi: UserApi,
    private val dataStore: DataStoreHelper
) {
    fun getUserProfile(): Flow<UserProfile> {
        return flowOf(true)
            .map {
                val token = dataStore.getAppToken().first() ?: throw NoDataException()
                userApi.getUserProfile(token)
            }.catch {
                throw it
                //TODO(예외 처리 필요)
            }
    }

    fun getLocalUserProfile(): Flow<UserProfile> {
        return dataStore.getUserProfile().catch {
                throw it
                //TODO(예외 처리 필요)
            }
    }

    suspend fun fetchUserProfile(userProfile: UserProfile) {
        dataStore.fetchUserProfile(userProfile)
    }

    fun login(email: String, password: String): Flow<Boolean> = flow {

        // todo API 호출, DataStore 저장

        dataStore.storeAppToken(email, password)
        emit(true)
    }

    fun signUp(email: String, password: String): Flow<Boolean> = flow {
        // todo API 호출, DataStore 저장

        dataStore.storeAppToken(email, password)
        emit(true)
    }

    fun checkEmailDuplicate(email: String): Flow<Unit> = flowOf(true)
        .map {

        }.catch {
            throw Exception()
        }


}