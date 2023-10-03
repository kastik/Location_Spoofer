package com.kastik.locationspoofer.ui.screens

import android.location.Location
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.LocationSource
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.kastik.locationspoofer.data.MyViewModel

@Composable
fun MapScreen(
    viewModel: MyViewModel
) {
    viewModel.setCameraState(rememberCameraPositionState())

    GoogleMap(
        locationSource = if (viewModel.mapCustomLocationProviderEnabled().value) {
            LocationSource(viewModel)
        } else {
            null
        },
        modifier = Modifier.fillMaxSize(),
        properties = MapProperties(isMyLocationEnabled = viewModel.mapMyLocationEnabled().value),
        uiSettings = MapUiSettings(
            myLocationButtonEnabled = false,
            zoomControlsEnabled = false,
            mapToolbarEnabled = false
        ),
        cameraPositionState = viewModel.getCameraState().value,
        onPOIClick = { viewModel.setMarker(it.latLng) },
        onMapClick = { viewModel.setMarker(null) },
        onMapLongClick = { viewModel.setMarker(it) },

        ) {
        if (viewModel.getMarker().value != null) {
            Marker(state = MarkerState(viewModel.getMarker().value!!))
            LaunchedEffect(viewModel.getMarker().value) {
                viewModel.getCameraState().value.animate(CameraUpdateFactory.newLatLng(viewModel.getMarker().value!!))
                viewModel.getCameraState().value.animate(CameraUpdateFactory.zoomTo(13f))
            }
        }
    }
}

class LocationSource(private val viewModel: MyViewModel) : LocationSource {
    override fun activate(p0: LocationSource.OnLocationChangedListener) {
        val location = Location("")
        location.latitude = viewModel.getSpoofedLatLng().value.latitude
        location.longitude = viewModel.getSpoofedLatLng().value.longitude
        //viewModel.myLocationEnabled().value = true
        //location.altitude = viewModel.getMarker().value!!.
        p0.onLocationChanged(location)
    }

    override fun deactivate() {
        viewModel.disableSpoofing()
    }

}