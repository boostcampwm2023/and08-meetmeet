package com.teameetmeet.meetmeet.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.teameetmeet.meetmeet.data.NoDataException
import com.teameetmeet.meetmeet.data.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataStoreHelper @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    suspend fun storeAppToken(accessToken: String, refreshToken: String) {
        dataStore.edit {
            it[ACCESS_TOKEN] = accessToken
            it[REFRESH_TOKEN] = refreshToken
        }
    }

    fun getAppToken(): Flow<String?> = dataStore.data.map { it[ACCESS_TOKEN] }

    suspend fun fetchUserProfile(userProfile: UserProfile) {
        dataStore.edit {
            it[USER_PROFILE_IMAGE] = userProfile.profileImage.orEmpty()
            it[USER_NICKNAME] = userProfile.nickname
        }
    }

    fun getUserProfile(): Flow<UserProfile> {
        return dataStore.data
            .map {
                it[USER_PROFILE_IMAGE]
            }
            .combine(dataStore.data.map { it[USER_NICKNAME] }) { userProfileImage, userNickName ->
                userNickName ?: throw NoDataException()
                UserProfile(userProfileImage, userNickName)
            }
    }

    fun getAlarmState(): Flow<Boolean> {
        return dataStore.data
            .map { it[IS_PUSH_ALARM_ON] ?: false }
    }

    suspend fun storeAlarmState(isOn: Boolean) {
        dataStore.edit {
            it[IS_PUSH_ALARM_ON] = isOn
        }
    }

    suspend fun deleteAppToken() {
        dataStore.edit {
            it[ACCESS_TOKEN] = ""
            it[REFRESH_TOKEN] = ""
        }
    }

    suspend fun deleteUserProfile() {
        dataStore.edit {
            it[USER_PROFILE_IMAGE] = ""
            it[USER_NICKNAME] = ""
        }
    }

    companion object {
        val ACCESS_TOKEN = stringPreferencesKey("accessToken")
        val REFRESH_TOKEN = stringPreferencesKey("refreshToken")
        val USER_PROFILE_IMAGE = stringPreferencesKey("userProfileImage")
        val USER_NICKNAME = stringPreferencesKey("userNickName")
        val IS_PUSH_ALARM_ON = booleanPreferencesKey("isPushAlarmOn")
    }
}