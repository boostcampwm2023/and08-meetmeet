package com.teameetmeet.meetmeet.data.repository

import com.teameetmeet.meetmeet.data.network.entity.UserProfile
import com.teameetmeet.meetmeet.data.network.api.UserApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userApi: UserApi
) {
    fun getUserProfile(accessToken: String) : Flow<UserProfile> {
        return flowOf(true)
            .map{
               userApi.getUserProfile(accessToken)
            }.catch {
                throw it
                //TODO(예외 처리 필요)
            }
    }
}