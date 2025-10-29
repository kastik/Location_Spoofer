package com.kastik.locationspoofer.data.datasource.local

import androidx.datastore.core.DataStore
import com.kastik.locationspoofer.DarkMode
import com.kastik.locationspoofer.UserPreferences
import kotlinx.coroutines.flow.Flow

class UserPreferencesLocalDataSource(private val userPreferencesRepo: DataStore<UserPreferences>) {

    val userPreferencesFlow: Flow<UserPreferences> = userPreferencesRepo.data

    suspend fun setResolveRoutes(value: Boolean){
        userPreferencesRepo.updateData { currentData ->
            currentData.toBuilder()
                .setResolveRoutes(value)
                .build()
        }
    }

    suspend fun setDarkMode(value: DarkMode){
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
                .setDisableMarkerZooming(value)
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

    suspend fun setEnableStatusBarSavedRoutes(value: Boolean){
        userPreferencesRepo.updateData { currentData ->
            currentData.toBuilder()
                .setEnableStatusBarSavedRoutes(value)
                .build()
        }
    }

}