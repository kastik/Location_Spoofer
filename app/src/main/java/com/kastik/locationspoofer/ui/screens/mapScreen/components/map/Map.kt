package com.kastik.locationspoofer.ui.screens.mapScreen.components.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.ButtCap
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.kastik.locationspoofer.LocationMockServiceState
import com.kastik.locationspoofer.data.models.MarkerData
import com.kastik.locationspoofer.data.models.toMarkerData
import com.kastik.locationspoofer.ui.screens.mapScreen.MapScreenState
import com.kastik.locationspoofer.ui.screens.mapScreen.SpoofedLocationSource
import com.kastik.locationspoofer.ui.theme.DarkMapTheme

@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun Map(
    serviceState: LocationMockServiceState,
    mapScreenState: MapScreenState,
    isDarkMode: Boolean,
    animateCameraToUser: () -> Unit,
    cameraState: CameraPositionState,
    markerData: List<MarkerData>,
    addMarker: (MarkerData) -> Unit,
    removeMarker:(MarkerData) -> Unit,
    clearAllMarkers:() -> Unit,
    polyline: List<LatLng>
){
    GoogleMap(
        modifier = Modifier.Companion.fillMaxSize(),
        locationSource = if (serviceState is LocationMockServiceState.MockingLocation) {
            SpoofedLocationSource(serviceState.latLng)
        } else {
            null
        },
        properties = MapProperties(
            mapType = MapType.NORMAL,
            mapStyleOptions = if (isDarkMode) {
                MapStyleOptions(
                    DarkMapTheme
                )
            } else {
                null
            },
            isMyLocationEnabled = mapScreenState is MapScreenState.Location || serviceState is LocationMockServiceState.MockingLocation //Note to self, "Don't enable it with spoof data or crash :)"
        ),
        uiSettings = MapUiSettings(
            myLocationButtonEnabled = false,
            zoomControlsEnabled = false,
            mapToolbarEnabled = false,
            compassEnabled = false
        ),
        cameraPositionState = cameraState,
        onPOIClick = { addMarker(it.toMarkerData()) },
        onMapClick = { clearAllMarkers() },
        onMapLongClick = { addMarker(it.toMarkerData()) },
    ) {

        if (polyline.isNotEmpty()) {
            Polyline(
                points = polyline,
                visible = true,
                endCap = ButtCap(),
                startCap = ButtCap(),
                jointType = JointType.ROUND,
                color = Color.Companion.Green.copy(alpha = 0.8f),
                //pattern = listOf(Dash(),Dot()),
                zIndex = 5f,
                width = 15f
            )
        }

        if (markerData.isNotEmpty()) {
            markerData.forEach { marker ->
                Marker(
                    state = MarkerState(marker.latLng),
                    title = "marker.placeId",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA),
                    snippet = "marker.name",
                    tag = "hjbjhbjhbhjbjbj",
                    onClick = {
                        removeMarker(marker)
                        true
                    })
            }
        }

        if (markerData.isNotEmpty()) {
            MapEffect(markerData.size) {
                cameraState.animate(
                    CameraUpdateFactory.newLatLngZoom(
                        markerData.last().latLng, 13f
                    )
                )
                if (cameraState.position.zoom < 13f) {
                    cameraState.animate(CameraUpdateFactory.zoomTo(13f))
                }
            }
        }

        MapEffect(mapScreenState) {
            when (mapScreenState) {
                is MapScreenState.Location -> {
                    animateCameraToUser()
                }

                else -> {}

            }
        }


    }

}