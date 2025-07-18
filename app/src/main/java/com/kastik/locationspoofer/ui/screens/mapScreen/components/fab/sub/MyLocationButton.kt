package com.kastik.locationspoofer.ui.screens.mapScreen.components.fab.sub

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kastik.locationspoofer.LocationMockServiceState
import com.kastik.locationspoofer.ui.screens.mapScreen.MapScreenState

@Composable
fun MyLocationButton(
    mapScreenState: MapScreenState,
    serviceState: LocationMockServiceState,
    moveCameraToUser: () -> Unit
) {
    AnimatedVisibility(
        mapScreenState is MapScreenState.Location || serviceState is LocationMockServiceState.MockingLocation,
        enter = scaleIn(),
        exit = scaleOut()
    ) {
        FloatingActionButton(
            modifier = Modifier.padding(top = 6.dp),
            onClick = moveCameraToUser,
            content = {
                Icon(Icons.Default.MyLocation, contentDescription = "My Location")
            })
    }
}