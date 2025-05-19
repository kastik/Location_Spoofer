package com.kastik.locationspoofer.ui.screens.settingsScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kastik.locationspoofer.debug.UserPreferences
import com.kastik.locationspoofer.ui.screens.settingsScreen.components.SettingSwitch
import com.kastik.locationspoofer.ui.theme.LocationSpooferTheme


@Composable
fun SettingsScreen() {
    val viewModel = hiltViewModel<SettingsScreenViewModel>()
    val preferencesState = viewModel.preferences.collectAsState(UserPreferences.newBuilder().build())
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(WindowInsets.systemBars.asPaddingValues())
            .padding(16.dp), // your own internal padding
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SettingSwitch(
            text = "Enable XPosed",
            checked = preferencesState.value.enableXposed,
            onCheckedChange = { viewModel.setXposed(it) })
        SettingSwitch(
            text = "AutoZoom Marker",
            checked = preferencesState.value.autoZoomMarker,
            onCheckedChange = { viewModel.enableMarkerZooming(it) })
        SettingSwitch(
            text = "Dark Mode",
            checked = preferencesState.value.darkMode,
            onCheckedChange = { viewModel.setDarkMode(it) })
    }
}