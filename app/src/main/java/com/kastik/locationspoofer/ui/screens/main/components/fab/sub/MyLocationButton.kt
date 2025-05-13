package com.kastik.locationspoofer.ui.screens.main.components.fab.sub

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
import com.google.android.gms.maps.model.LatLng
import com.kastik.locationspoofer.ui.screens.main.components.map.MapScreenState

@Composable
fun MyLocationButton(
    mapScreenState: MapScreenState,
    updateMapScreenState: () -> Unit,
    moveCameraTo: (LatLng) -> Unit
) {
    AnimatedVisibility(
        mapScreenState is MapScreenState.LoadedLocation || mapScreenState is MapScreenState.SpoofingLocation,
        enter = scaleIn(),
        exit = scaleOut()
    ) {
        FloatingActionButton(modifier = Modifier.padding(top = 6.dp), onClick = {
                if (mapScreenState is MapScreenState.SpoofingLocation) {
                    moveCameraTo(mapScreenState.spoofedLocation)
                } else {
                    if (mapScreenState is MapScreenState.LoadedLocation) {
                        updateMapScreenState()
                        moveCameraTo(mapScreenState.userLocation)
                        //TODO hasLocationPermission(context, hasLocationPermissions)
                    } else {
                        //TODO This shouldn't happen, TEST IT THOUGH!
                    }
                }

        }, content = {
            Icon(Icons.Default.MyLocation, contentDescription = "My Location")
        })
    }
}