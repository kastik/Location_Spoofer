package com.kastik.locationspoofer.ui.screens.settingsScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kastik.locationspoofer.debug.UserPreferences


@Composable
fun SettingsScreen() {
    val viewModel = hiltViewModel<SettingsScreenViewModel>()
    val state = viewModel.preferences.collectAsState(UserPreferences.newBuilder().build())
    Column(Modifier.fillMaxSize()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Enable XPosed", modifier = Modifier.weight(1f))
            Switch(
                checked = state.value.enableXposed,
                onCheckedChange = { viewModel.setXposed(it) },
                modifier = Modifier.weight(1f))
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("AutoZoom Marker", modifier = Modifier.weight(1f))
            Switch(
                checked = state.value.autoZoomMarker,
                onCheckedChange = { viewModel.enableMarkerZooming(it) },
                modifier = Modifier.weight(1f))
        }
    }
}