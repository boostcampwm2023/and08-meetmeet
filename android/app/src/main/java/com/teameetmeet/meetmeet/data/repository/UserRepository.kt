package com.teameetmeet.meetmeet.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.teameetmeet.meetmeet.data.network.api.UserApi
import com.teameetmeet.meetmeet.data.network.entity.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userApi: UserApi,
    private val dataStore: DataStore<Preferences>
) {
    fun getUserProfile(accessToken: String): Flow<UserProfile> {
        return flowOf(true)
            .map {
                userApi.getUserProfile(accessToken)
            }.catch {
                throw it
                //TODO(예외 처리 필요)
            }
    }

    fun login(email: String, password: String): Flow<Boolean> = flow {

        // todo API 호출, DataStore 저장

        dataStore.edit {
            it[ACCESS_TOKEN] = email
            it[REFRESH_TOKEN] = password
        }
        emit(true)
    }

    fun signUp(email: String, password: String): Flow<Boolean> = flow {
        // todo API 호출, DataStore 저장

        dataStore.edit {
            it[ACCESS_TOKEN] = email
            it[REFRESH_TOKEN] = password
        }
        emit(true)
    }

    fun checkEmailDuplicate(email: String): Flow<Unit> = flowOf(true)
        .map {

        }.catch {
            throw Exception()
        }

    fun getToken(): Flow<String?> =
        dataStore.data.map { it[ACCESS_TOKEN] }

    companion object {
        val ACCESS_TOKEN = stringPreferencesKey("accessToken")
        val REFRESH_TOKEN = stringPreferencesKey("refreshToken")
    }
}