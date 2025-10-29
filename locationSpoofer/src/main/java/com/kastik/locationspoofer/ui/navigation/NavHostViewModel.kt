package com.kastik.locationspoofer.ui.navigation

import androidx.lifecycle.ViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.kastik.locationspoofer.data.datasource.local.UserPreferencesLocalDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@OptIn(ExperimentalPermissionsApi::class)
@HiltViewModel
class NavHostViewModel @OptIn(ExperimentalPermissionsApi::class) @Inject constructor(
    preferencesRepo: UserPreferencesLocalDataSource,
) : ViewModel() {
    val userPreferencesFlow = preferencesRepo.userPreferencesFlow
}
