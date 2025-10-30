package com.kastik.locationspoofer.ui.screens.mapScreen.components.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.ButtCap
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.kastik.locationspoofer.data.mapers.toGmsLatLng
import com.kastik.locationspoofer.data.mapers.toPlaceDomain
import com.kastik.locationspoofer.decodePolyline
import com.kastik.locationspoofer.domain.model.LatLngDomain
import com.kastik.locationspoofer.domain.model.PlaceDomain
import com.kastik.locationspoofer.domain.model.RouteDomain
import com.kastik.locationspoofer.ui.screens.mapScreen.SpoofedLocationSource
import com.kastik.locationspoofer.ui.theme.DarkMapTheme

@OptIn(MapsComposeExperimentalApi::class, ExperimentalPermissionsApi::class)
@Composable
fun Map(
    modifier: Modifier = Modifier,
    spoofingLocation: LatLng?,
    hasLocationEnabled: Boolean,
    isDarkMode: Boolean,
    animateCameraToUser: () -> Unit,
    cameraState: CameraPositionState,
    placedMarkers: List<LatLngDomain>,
    addMarker: (PlaceDomain) -> Unit,
    removeMarker: (LatLngDomain) -> Unit,
    clearAllMarkers: () -> Unit,
    route: RouteDomain?,
) {

    val mapUiSettings = remember {
        MapUiSettings(
            compassEnabled = false,
            myLocationButtonEnabled = false,
            zoomControlsEnabled = false,
            mapToolbarEnabled = false
        )
    }


    val mapStyle = remember(isDarkMode) {
        if (isDarkMode) {
            MapStyleOptions(DarkMapTheme)
        } else null
    }

    val locationSource = remember(spoofingLocation, hasLocationEnabled) {
        spoofingLocation?.let {
            SpoofedLocationSource(it)
        }
    }

    val mapProperties = remember(hasLocationEnabled, spoofingLocation, mapStyle) {
        MapProperties(
            isMyLocationEnabled = hasLocationEnabled || spoofingLocation != null,
            mapStyleOptions = mapStyle
        )
    }



    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraState,
        locationSource = locationSource,
        properties = mapProperties,
        uiSettings = mapUiSettings,
        onPOIClick = { poi ->
            addMarker(poi.toPlaceDomain())
        },
        onMapClick = { clearAllMarkers() },
        onMapLongClick = { latLng ->
            addMarker(latLng.toPlaceDomain())
        }) {
        route?.let {
            Polyline(
                points = decodePolyline(route.encodedPolyline), //TODO TO POINTS
                visible = true,
                endCap = ButtCap(),
                startCap = ButtCap(),
                jointType = JointType.ROUND,
                color = Color(0xFF0E4DEC),
                //pattern = listOf(Dash(),Dot()),
                zIndex = 5f,
                width = 15f
            )
        }
        placedMarkers.forEach { marker ->
            Marker(
                state = MarkerState(marker.toGmsLatLng()), onClick = {
                    removeMarker(marker)
                    true
                    //TODO Why is true needed?
                })
        }

        MapEffect(placedMarkers.size) {
            if (placedMarkers.isNotEmpty()) {
                cameraState.animate(
                    CameraUpdateFactory.newLatLngZoom(
                        placedMarkers.last().toGmsLatLng(), 13f
                    )
                )
                if (cameraState.position.zoom < 13f) {
                    cameraState.animate(CameraUpdateFactory.zoomTo(13f))
                }
            }

        }
        MapEffect(hasLocationEnabled) {
            if (hasLocationEnabled) {
                animateCameraToUser()
            }
        }
    }

}