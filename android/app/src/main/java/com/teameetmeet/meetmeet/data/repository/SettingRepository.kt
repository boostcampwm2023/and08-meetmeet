package com.teameetmeet.meetmeet.data.repository

import com.teameetmeet.meetmeet.data.local.datastore.DataStoreHelper
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingRepository @Inject constructor(
    private val dataStore: DataStoreHelper
) {
    fun getAlarmState(): Flow<Boolean> {
        return dataStore.getAlarmState()
    }

    suspend fun storeAlarmState(isOn: Boolean) {
        dataStore.storeAlarmState(isOn)
    }
}