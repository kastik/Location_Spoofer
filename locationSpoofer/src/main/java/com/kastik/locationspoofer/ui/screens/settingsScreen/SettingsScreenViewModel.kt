package com.kastik.locationspoofer.ui.screens.settingsScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kastik.locationspoofer.DarkMode
import com.kastik.locationspoofer.data.datasource.local.UserPreferencesLocalDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val userPreferencesLocalDataSource: UserPreferencesLocalDataSource
) : ViewModel() {
    val preferences = userPreferencesLocalDataSource.userPreferencesFlow

    fun setXposed(value: Boolean) {
        viewModelScope.launch {
            userPreferencesLocalDataSource.setEnableXposed(value)
        }
    }

    fun enableMarkerZooming(value: Boolean) {
        viewModelScope.launch {
            userPreferencesLocalDataSource.setEnableMarkerZooming(value)
        }
    }

    fun setDarkMode(value: DarkMode){
        viewModelScope.launch {
            userPreferencesLocalDataSource.setDarkMode(value)
        }
    }

    fun setStatusBar(value: Boolean){
        viewModelScope.launch {
            userPreferencesLocalDataSource.setEnableStatusBarSavedRoutes(value)
        }
    }

}
