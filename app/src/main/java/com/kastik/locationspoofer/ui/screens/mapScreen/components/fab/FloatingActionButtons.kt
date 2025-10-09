package com.kastik.locationspoofer.ui.screens.mapScreen.components.fab

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.kastik.locationspoofer.service.LocationMockServiceState
import com.kastik.locationspoofer.ui.screens.mapScreen.components.fab.sub.SavedPlacesButton
import com.kastik.locationspoofer.ui.screens.mapScreen.MapScreenState
import com.kastik.locationspoofer.ui.screens.mapScreen.components.fab.sub.LocationSpoofButton
import com.kastik.locationspoofer.ui.screens.mapScreen.components.fab.sub.MyLocationButton
import com.kastik.locationspoofer.ui.screens.mapScreen.components.fab.sub.SaveLocationButton

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FloatingActionButtons(
    hasPlacedPolylineOrMarker: Boolean,
    serviceState: LocationMockServiceState,
    moveCameraToUser: () -> Unit,
    savePlace: () -> Unit,
    showSaveButton: Boolean,
    isPlaceSaved: Boolean,
    removeSavedPlace: () -> Unit,
    stopSpoofing: () -> Unit,
    startSpoofing: () -> Unit,
    navigateToSavedRoutesScreen: () -> Unit,
    showSavedPlaces: Boolean
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
        AnimatedVisibility(showSavedPlaces) {
            SavedPlacesButton(
                navigateToSavedRoutesScreen
            )
        }
        MyLocationButton(
            moveCameraToUser = moveCameraToUser,
            serviceState = serviceState
        )
    }
}