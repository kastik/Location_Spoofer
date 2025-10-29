package com.kastik.locationspoofer.ui.screens.mapScreen.components.fab.sub

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MyLocationButton(
    moveCameraToUser: () -> Unit
) {
    FloatingActionButton(
        modifier = Modifier.padding(top = 6.dp),
        onClick = moveCameraToUser,
        content = {
            Icon(Icons.Default.MyLocation, contentDescription = "My Location")
        })
}