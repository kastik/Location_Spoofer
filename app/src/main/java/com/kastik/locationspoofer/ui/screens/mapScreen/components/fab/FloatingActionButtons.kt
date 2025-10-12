package com.kastik.locationspoofer.ui.screens.mapScreen.components.fab

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.kastik.locationspoofer.ui.screens.mapScreen.components.fab.sub.LocationSpoofButton
import com.kastik.locationspoofer.ui.screens.mapScreen.components.fab.sub.MyLocationButton
import com.kastik.locationspoofer.ui.screens.mapScreen.components.fab.sub.SaveLocationButton
import com.kastik.locationspoofer.ui.screens.mapScreen.components.fab.sub.SavedPlacesButton

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FloatingActionButtons(
    modifier: Modifier = Modifier,
    navigateToSavedRoutes: () -> Unit,
    stopSpoofing: () -> Unit,
    startSpoofing: () -> Unit,
    hasPlacedPolylineOrMarker: Boolean,
    isSpoofing: Boolean,
    moveCameraToUser: () -> Unit,
    savePlace: () -> Unit,
    showSaveButton: Boolean,
    isPlaceSaved: Boolean,
    unSavePlace: () -> Unit,

    ) {
    Column(modifier = modifier) {
        LocationSpoofButton(
            stopSpoofing = stopSpoofing,
            startSpoofing = startSpoofing,
            isSpoofing = isSpoofing,
            hasPlacedMarkerOrPolyline = hasPlacedPolylineOrMarker,
        )
        SaveLocationButton(
            isPlaceSaved = isPlaceSaved,
            savePlace = savePlace,
            unSavePlace = unSavePlace,
            showSaveButton = showSaveButton,
        )
        SavedPlacesButton(
            navigateToSavedRoutes = navigateToSavedRoutes
        )

        MyLocationButton(
            moveCameraToUser = moveCameraToUser
        )
    }
}