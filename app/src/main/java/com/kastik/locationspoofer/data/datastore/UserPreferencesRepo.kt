package com.kastik.locationspoofer.data.datastore

import androidx.datastore.core.DataStore
import com.kastik.locationspoofer.debug.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

class UserPreferencesRepo(private val userPreferencesRepo: DataStore<UserPreferences>) {

    val userPreferencesFlow: Flow<UserPreferences> = userPreferencesRepo.data
        .catch {  } //TODO?

    suspend fun setDarkMode(value: Boolean){
        userPreferencesRepo.updateData { currentData ->
            currentData.toBuilder()
                .setDarkMode(value)
                .build()
        }
    }

    suspend fun setEnableXposed(value: Boolean){
        userPreferencesRepo.updateData { currentData ->
            currentData.toBuilder()
                .setEnableXposed(value)
                .build()
        }
    }
    suspend fun setEnableMarkerZooming(value: Boolean){
        userPreferencesRepo.updateData { currentData ->
            currentData.toBuilder()
                .setAutoZoomMarker(value)
                .build()
        }
    }

    suspend fun setDeniedLocationPermission(value: Boolean){
        userPreferencesRepo.updateData { currentData ->
            currentData.toBuilder()
                .setDeniedLocation(value)
                .build()
        }
    }

    suspend fun setAskedLocation(value: Boolean){
        userPreferencesRepo.updateData { currentData ->
            currentData.toBuilder()
                .setAskedLocation(value)
                .build()
        }
    }

    suspend fun setDeniedNotificationPermission(value: Boolean){
        userPreferencesRepo.updateData { currentData ->
            currentData.toBuilder()
                .setDeniedNotifications(value)
                .build()
        }
    }

    suspend fun setAskedNotificationPermission(value: Boolean){
        userPreferencesRepo.updateData { currentData ->
            currentData.toBuilder()
                .setAskedNotification(value)
                .build()
        }
    }

    suspend fun setDeniedMock(value: Boolean){
        userPreferencesRepo.updateData { currentData ->
            currentData.toBuilder()
                .setDeniedMock(value)
                .build()
        }
    }

}