package com.kastik.locationspoofer.ui.screens.main.components.map

import android.location.Location
import android.location.LocationManager
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.isGranted
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.LocationSource
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.kastik.locationspoofer.data.models.MarkerData
import com.kastik.locationspoofer.data.models.toMarkerData

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    markerData: MarkerData?,
    setMarkerData: (MarkerData?) -> Unit,
    locationPermissionState: MultiplePermissionsState,
    cameraState: CameraPositionState,
    mapScreenState: MapScreenState
) {
    GoogleMap(
        modifier = Modifier.Companion.fillMaxSize(),
        locationSource = if (mapScreenState is MapScreenState.SpoofingLocation) {
            SpoofedLocationSource(mapScreenState.spoofedLocation)
        } else {
            null
        },
        properties = MapProperties(
            isMyLocationEnabled =
                locationPermissionState.permissions[0].status.isGranted || mapScreenState is MapScreenState.SpoofingLocation //Note to self, "Don't enable it with spoof data or crash :)"
        ),
        uiSettings = MapUiSettings(
            myLocationButtonEnabled = false,
            zoomControlsEnabled = false,
            mapToolbarEnabled = false
        ),
        cameraPositionState = cameraState,
        onPOIClick = { setMarkerData(it.toMarkerData()) },
        onMapClick = { setMarkerData(null) },
        onMapLongClick = { setMarkerData(it.toMarkerData()) },
    ) {
        if (markerData != null) {
            Marker(
                state = MarkerState(markerData.latLng),
                //icon = BitmapDescriptorFactory.fromBitmap()// TODO Change the default marker icon
            )
            LaunchedEffect(markerData) {
                cameraState.animate(
                    CameraUpdateFactory.newLatLngZoom(
                        markerData.latLng, 13f
                    )
                )
                if (cameraState.position.zoom < 13f) {
                    cameraState.animate(CameraUpdateFactory.zoomTo(13f))
                }
            }
        }
    }

    LaunchedEffect(mapScreenState) {
        when (mapScreenState) {
            is MapScreenState.LoadedLocation -> {
                cameraState.animate(
                    CameraUpdateFactory.newLatLngZoom(
                        mapScreenState.userLocation, 13f
                    )
                )
            }

            else -> {}
        }
    }
}


class SpoofedLocationSource(
    val spoofedLocation: LatLng
) : LocationSource {
    override fun activate(p0: LocationSource.OnLocationChangedListener) {
        val location = Location(LocationManager.GPS_PROVIDER)
        location.latitude = spoofedLocation.latitude
        location.longitude = spoofedLocation.longitude
        p0.onLocationChanged(location)
    }

    override fun deactivate() {
        //Do i need a TODO here?
    }
}

sealed class MapScreenState {
    data object NoLocation : MapScreenState()
    class LoadedLocation(val userLocation: LatLng) : MapScreenState()
    class SpoofingLocation(val spoofedLocation: LatLng) : MapScreenState()
    class Error(val displayMsg: String, val displayTitle: String, val action: () -> Unit,val dismiss: () -> Unit) : MapScreenState()
}