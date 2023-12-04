package com.teameetmeet.meetmeet.data.repository

import com.teameetmeet.meetmeet.data.model.UserStatus
import com.teameetmeet.meetmeet.data.network.api.FollowApi
import com.teameetmeet.meetmeet.data.network.entity.FollowRequest
import com.teameetmeet.meetmeet.data.toException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FollowRepository @Inject constructor(
    private val followApi: FollowApi
) {

    fun follow(userId: Int): Flow<Unit> {
        return flowOf(true)
            .map {
                followApi.follow(FollowRequest(userId = userId))
            }.catch {
                throw it.toException()
            }
    }

    fun unFollow(userId: Int): Flow<Unit> {
        return flowOf(true)
            .map {
                followApi.unFollow(userId = userId)
            }.catch {
                throw it.toException()
            }
    }

    fun getFollowingWithFollowState(): Flow<List<UserStatus>> {
        return flowOf(true)
            .map {
                followApi.getFollowingWithFollowStatus().users
            }.catch {
                throw it
            }
    }

    fun getFollowerWithFollowState(): Flow<List<UserStatus>> {
        return flowOf(true)
            .map {
                followApi.getFollowerWithFollowStatus().users
            }.catch {
                throw it
            }
    }
}