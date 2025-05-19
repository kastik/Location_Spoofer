package com.kastik.locationspoofer.ui.screens.mapScreen.components.fab

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.kastik.locationspoofer.LocationMockServiceState
import com.kastik.locationspoofer.data.models.MarkerData
import com.kastik.locationspoofer.debug.Place
import com.kastik.locationspoofer.ui.screens.mapScreen.MapScreenState
import com.kastik.locationspoofer.ui.screens.mapScreen.components.fab.sub.LocationSpoofButton
import com.kastik.locationspoofer.ui.screens.mapScreen.components.fab.sub.MyLocationButton
import com.kastik.locationspoofer.ui.screens.mapScreen.components.fab.sub.SaveLocationButton

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FloatingActionButtons(
    hasPlacedPolylineOrMarker: Boolean,
    mapScreenState: MapScreenState,
    serviceState: LocationMockServiceState,
    moveCameraToUser: () -> Unit,
    savePlace: () -> Unit,
    showSaveButton: Boolean,
    isPlaceSaved: State<Boolean>,
    removeSavedPlace: () -> Unit,
    stopSpoofing: () -> Unit,
    startSpoofing: () -> Unit,
) {
    Column {
        SaveLocationButton(
            isPlaceSaved = isPlaceSaved,
            savePlace = savePlace,
            removeSavedPlace = removeSavedPlace,
            showSaveButton = showSaveButton,

        )
        LocationSpoofButton(
            stopSpoofing = stopSpoofing,
            startSpoofing = startSpoofing,
            serviceState = serviceState,
            hasPlacedMarkerOrPolyline = hasPlacedPolylineOrMarker,
        )
        MyLocationButton(
            mapScreenState = mapScreenState,
            moveCameraToUser = moveCameraToUser,
            serviceState = serviceState
        )
    }
}