package com.teameetmeet.meetmeet.data.repository

import com.teameetmeet.meetmeet.data.NoDataException
import com.teameetmeet.meetmeet.data.local.datastore.DataStoreHelper
import com.teameetmeet.meetmeet.data.model.UserProfile
import com.teameetmeet.meetmeet.data.network.api.UserApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userApi: UserApi,
    private val dataStore: DataStoreHelper
) {
    fun getUserProfile(): Flow<UserProfile> {
        return flowOf(true)
            .map {
                val token = dataStore.getAppToken().first() ?: throw NoDataException()
                val result = userApi.getUserProfile()
                result
            }.onEach {
                fetchUserProfile(it)
            }.catch {
                getLocalUserProfile()
            }
    }

    fun getToken(): Flow<String?> {
        return dataStore.getAppToken()
            .catch {
                throw it
                //TODO("예외 처리 필요")
            }
    }

    private fun getLocalUserProfile(): Flow<UserProfile> {
        return dataStore.getUserProfile().catch {
            throw it
            //TODO(예외 처리 필요)
        }
    }

    private suspend fun fetchUserProfile(userProfile: UserProfile) {
        dataStore.fetchUserProfile(userProfile)
    }

    fun resetDataStore(): Flow<Unit> {
        return flowOf(true)
            .map {
                dataStore.resetAlarmState()
                dataStore.deleteUserProfile()
                dataStore.deleteAppToken()
            }.catch {
                throw it
            }

    }

    fun deleteUser(): Flow<Unit> {
        return flowOf(true)
            .map {
                userApi.deleteUser()
                dataStore.deleteUserProfile()
                dataStore.deleteAppToken()
            }.catch {
                throw it
            }
    }

    fun checkNickNameDuplication(nickname: String): Flow<Boolean> {
        return flowOf(true)
            .map {
                val response = userApi.checkNickNameDuplication(nickname)
                response.isAvailable
            }.catch {
                throw it
            }
    }

    fun patchUserProfile(image: File?, nickname: String): Flow<UserProfile> {
        return flowOf(true)
            .map {
                val profileImageRequest = if (image == null) {
                    null
                } else {
                    MultipartBody.Part.createFormData(
                        "profile",
                        image.name,
                        image.asRequestBody()
                    )
                }
                val nicknameRequest = nickname.toRequestBody("text/plain".toMediaType())
                val response = userApi.updateUserProfile(nicknameRequest, profileImageRequest)
                fetchUserProfile(response)
                response
            }
    }
}