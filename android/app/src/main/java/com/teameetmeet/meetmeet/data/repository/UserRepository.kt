package com.teameetmeet.meetmeet.data.repository

import com.teameetmeet.meetmeet.data.local.datastore.DataStoreHelper
import com.teameetmeet.meetmeet.data.model.UserProfile
import com.teameetmeet.meetmeet.data.model.UserStatus
import com.teameetmeet.meetmeet.data.network.api.UserApi
import com.teameetmeet.meetmeet.data.network.entity.NicknameChangeRequest
import com.teameetmeet.meetmeet.data.network.entity.PasswordChangeRequest
import com.teameetmeet.meetmeet.data.network.entity.TokenRequest
import com.teameetmeet.meetmeet.data.toException
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
                userApi.getUserProfile()
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

    fun getUserWithFollowStatus(nickname: String): Flow<UserStatus> {
        return flowOf(true)
            .map {
                val userNickname = dataStore.getUserProfile().first().nickname
                val user = userApi.getUserWithFollowStatus(nickname)
                if (user.nickname == userNickname) {
                    user.copy(isMe = true)
                } else {
                    user
                }
            }.catch {
                throw it.toException()
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

    fun patchPassword(password: String): Flow<UserProfile> {
        return flowOf(true)
            .map {
                userApi.patchPassword(PasswordChangeRequest(password))
            }.catch {
                throw it
            }
    }

    fun patchNickname(nickname: String): Flow<Unit> {
        return flowOf(true)
            .map {
                userApi.patchNickname(NicknameChangeRequest(nickname))
            }.catch {
                throw it
            }
    }

    fun patchProfileImage(image: File?): Flow<Unit> {
        return flowOf(true)
            .map {
                val profileImageRequest = if (image == null) {
                    MultipartBody.Part.createFormData(
                        "profile",
                        "",
                        "".toRequestBody("text/plain".toMediaType())
                    )
                } else {
                    MultipartBody.Part.createFormData(
                        "profile",
                        image.name,
                        image.asRequestBody()
                    )
                }
                userApi.updateProfileImage(profileImageRequest)
            }.catch {
                throw it
            }
    }

    suspend fun updateFcmToken(token: String) {
        try {
            userApi.updateFcmToken(TokenRequest(token))
        } catch (e:Exception) {
            //todo: 예외처리
        }
    }
}