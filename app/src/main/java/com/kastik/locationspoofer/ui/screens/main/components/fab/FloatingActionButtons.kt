package com.kastik.locationspoofer.ui.screens.main.components.fab

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.maps.model.LatLng
import com.kastik.locationspoofer.data.models.MarkerData
import com.kastik.locationspoofer.debug.Place
import com.kastik.locationspoofer.ui.screens.main.components.fab.sub.LocationSpoofButton
import com.kastik.locationspoofer.ui.screens.main.components.fab.sub.MyLocationButton
import com.kastik.locationspoofer.ui.screens.main.components.fab.sub.SaveLocationButton
import com.kastik.locationspoofer.ui.screens.main.components.map.MapScreenState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FloatingActionButtons(
    marker: MarkerData?,
    mapScreenState: MapScreenState,
    moveCameraTo: (LatLng) -> Unit,
    savePlace: (place: Place) -> Unit,
    isPlaceSaved: State<Boolean>,
    removeSavedPlace: () -> Unit,
    stopSpoofing: () -> Unit,
    startSpoofing: () -> Unit
) {
    Column {
        SaveLocationButton(
            isPlaceSaved = isPlaceSaved,
            marker = marker,
            savePlace = savePlace,
            removeSavedPlace = removeSavedPlace
        )
        LocationSpoofButton(
            marker = marker,
            mapScreenState = mapScreenState,
            stopSpoofing = stopSpoofing,
            startSpoofing = startSpoofing,
        )
        MyLocationButton(
            mapScreenState = mapScreenState,
            moveCameraTo = moveCameraTo,
            //TODO This is for testing only update it properly
            updateMapScreenState = stopSpoofing

        )
    }
}