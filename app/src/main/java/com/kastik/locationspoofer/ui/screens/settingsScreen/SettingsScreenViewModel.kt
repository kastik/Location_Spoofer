package com.kastik.locationspoofer.ui.screens.settingsScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kastik.locationspoofer.data.datastore.UserPreferencesRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val userPreferencesRepo: UserPreferencesRepo
) : ViewModel() {
    val preferences = userPreferencesRepo.userPreferencesFlow

    fun setXposed(value: Boolean) {
        viewModelScope.launch {
            userPreferencesRepo.setEnableXposed(value)
        }
    }

    fun enableMarkerZooming(value: Boolean) {
        viewModelScope.launch {
            userPreferencesRepo.setEnableMarkerZooming(value)
        }
    }

    fun setDarkMode(value: Boolean){
        viewModelScope.launch {
            userPreferencesRepo.setDarkMode(value)
        }
    }

}
