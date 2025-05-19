package com.kastik.locationspoofer.ui.screens.main

import androidx.lifecycle.ViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.kastik.locationspoofer.data.datastore.SavedPlacesRepo
import com.kastik.locationspoofer.data.datastore.UserPreferencesRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@OptIn(ExperimentalPermissionsApi::class)
@HiltViewModel
class MainUIViewModel @OptIn(ExperimentalPermissionsApi::class) @Inject constructor(
    private val savedPlacesRepo: SavedPlacesRepo,
    private val preferencesRepo: UserPreferencesRepo
) : ViewModel() {

}
