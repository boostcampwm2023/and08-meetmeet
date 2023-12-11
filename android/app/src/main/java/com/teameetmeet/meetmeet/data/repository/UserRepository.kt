package com.teameetmeet.meetmeet.data.repository

import android.net.Uri
import com.teameetmeet.meetmeet.data.local.datastore.DataStoreHelper
import com.teameetmeet.meetmeet.data.model.UserProfile
import com.teameetmeet.meetmeet.data.model.UserStatus
import com.teameetmeet.meetmeet.data.network.api.UserApi
import com.teameetmeet.meetmeet.data.network.entity.EventInvitationNotification
import com.teameetmeet.meetmeet.data.network.entity.FollowNotification
import com.teameetmeet.meetmeet.data.network.entity.NicknameChangeRequest
import com.teameetmeet.meetmeet.data.network.entity.PasswordChangeRequest
import com.teameetmeet.meetmeet.data.network.entity.TokenRequest
import com.teameetmeet.meetmeet.data.toException
import com.teameetmeet.meetmeet.util.getMimeType
import com.teameetmeet.meetmeet.util.toAbsolutePath
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

    fun logout(): Flow<Unit> {
        return flowOf(true)
            .map {
                userApi.logout()
            }.catch {
                it.toException()
            }
    }

    private fun getLocalUserProfile(): Flow<UserProfile> {
        return dataStore.getUserProfile().catch {
            throw it
            //TODO(예외 처리 필요)
        }
    }

    fun getUserWithFollowStatus(nickname: String): Flow<List<UserStatus>> {
        return flowOf(true)
            .map {
                val userNickname = dataStore.getUserProfile().first().nickname
                userApi.getUserWithFollowStatus(nickname).users.map {
                    if (it.nickname == userNickname) {
                        it.copy(isMe = true)
                    } else {
                        it
                    }
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
                throw it.toException()
            }
    }

    fun checkNickNameDuplication(nickname: String): Flow<Boolean> {
        return flowOf(true)
            .map {
                val response = userApi.checkNickNameDuplication(nickname)
                response.isAvailable
            }.catch {
                throw it.toException()
            }
    }

    fun patchPassword(password: String): Flow<UserProfile> {
        return flowOf(true)
            .map {
                userApi.patchPassword(PasswordChangeRequest(password))
            }.catch {
                throw it.toException()
            }
    }

    fun patchNickname(nickname: String): Flow<Unit> {
        return flowOf(true)
            .map {
                userApi.patchNickname(NicknameChangeRequest(nickname))
            }.catch {
                throw it.toException()
            }
    }

    fun patchProfileImage(uri: Uri?): Flow<Boolean?> {
        return flowOf(true)
            .map {
                val imageFile = uri?.toAbsolutePath()?.let { File(it) }
                val profileImageRequest = if (imageFile == null) {
                    MultipartBody.Part.createFormData(
                        "profile",
                        "",
                        "".toRequestBody("text/plain".toMediaType())
                    )
                } else {
                    MultipartBody.Part.createFormData(
                        "profile",
                        imageFile.name,
                        imageFile.asRequestBody(uri.getMimeType()?.toMediaType())
                    )
                }
                userApi.updateProfileImage(profileImageRequest)
                imageFile?.delete()
            }.catch {
                throw it.toException()
            }
    }

    suspend fun updateFcmToken(token: String) {
        try {
            userApi.updateFcmToken(TokenRequest(token))
        } catch (e: Exception) {
            //todo: 예외처리
        }
    }

    fun getFollowNotification(): Flow<List<FollowNotification>> {
        return flowOf(true)
            .map {
                userApi.getFollowNotification().map { it.body }
            }.catch {
                throw it.toException()
            }
    }

    fun getEventInvitationNotification(): Flow<List<EventInvitationNotification>> {
        return flowOf(true)
            .map {
                userApi.getEventInvitationNotification().map { it.body }
            }.catch {
                throw it.toException()
            }
    }

    fun deleteUserNotification(ids: String): Flow<Unit> {
        return flowOf(true)
            .map {
                userApi.deleteUserNotification(ids)
            }.catch {
                throw it.toException()
            }
    }
}