package com.kastik.locationspoofer.ui.screens.mapScreen

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.LocationSource
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.rememberCameraPositionState
import com.kastik.locationspoofer.UpdateLocationService
import com.kastik.locationspoofer.debug.SavedPlaces
import com.kastik.locationspoofer.ui.screens.AvailableScreens
import com.kastik.locationspoofer.ui.screens.mapScreen.components.dialogs.ErrorDialog
import com.kastik.locationspoofer.ui.screens.mapScreen.components.fab.FloatingActionButtons
import com.kastik.locationspoofer.ui.screens.mapScreen.components.map.Map
import com.kastik.locationspoofer.ui.screens.mapScreen.components.searchbar.TopSearchBar
import com.kastik.locationspoofer.ui.screens.mapScreen.components.searchbar.sub.SearchBarChips
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


//TODO Proper permission checks
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalPermissionsApi::class, MapsComposeExperimentalApi::class)
@Composable
fun MapScreen(
    updateLocationService: UpdateLocationService,
    navigate: (String) -> Unit
) {

    val viewModel: MapScreenViewModel = hiltViewModel()
    viewModel.setServiceState(updateLocationService.serviceState.value)


    val mapScreenState = viewModel.mapScreenState
    val scope = rememberCoroutineScope()
    val cameraState = rememberCameraPositionState()
    val context = LocalContext.current
    val markerData = viewModel.markerState


    val savedPlacesState = viewModel.savedPlacesFlow.collectAsState(
        SavedPlaces.newBuilder().build()
    )

    val locationPermissionState = rememberMultiplePermissionsState(
        arrayListOf(
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
        )
    )

    val notificationPermissionState = rememberPermissionState(
        Manifest.permission.POST_NOTIFICATIONS
    )


    Scaffold(topBar = {
        TopSearchBar(
            visible = true,
            savedPlacesView = {
                SearchBarChips(
                    savedPlaces = savedPlacesState.value, placeMarker = { viewModel.addMarker(it) })
            },
            navigateToSettings = { navigate(AvailableScreens.Settings.name) },
            moveCamera = {
                viewModel.moveCamera(
                    LatLng(
                        it.latitude, it.longitude
                    )
                )
            },

            )

    }, floatingActionButton = {
        FloatingActionButtons(
            mapScreenState = viewModel.mapScreenState.value,
            moveCameraToUser = {
                scope.launch {
                    cameraState.animate(
                        CameraUpdateFactory.newLatLngZoom(
                            viewModel.getUserLocation(context), 13f
                        )
                    )
                }
            },
            savePlace = {
                viewModel.savePlace()
            },
            showSaveButton = viewModel.showSaveButton(),
            removeSavedPlace = { viewModel.deletePlace() },
            isPlaceSaved = viewModel.isPlaceSaved.collectAsState(false),
            stopSpoofing = {
                scope.launch {
                    viewModel.stopSpoofing(
                        binder = updateLocationService,
                        context = context,
                        hasLocationPermission = locationPermissionState.permissions[0].status.isGranted
                    )
                }
            },
            startSpoofing = {
                Log.d("MyLog", "In start Spoofing in map screen")
                viewModel.startMockingLocation(
                    binder = updateLocationService,
                    context = context,
                    notificationsGranted = notificationPermissionState.status.isGranted
                )

            },
            hasPlacedPolylineOrMarker = viewModel.hasPlacedMarkerOrPolyline(),
            serviceState = updateLocationService.serviceState.value
        )
    }) {
        AnimatedVisibility(mapScreenState.value is MapScreenState.Error) {
            val error = mapScreenState.value as? MapScreenState.Error
            when (error) {
                MapScreenState.Error.LocationError -> {
                    ErrorDialog(
                        title = "Location error",
                        message = "The location permission is strongly encouraged",
                        onCLick = {
                            locationPermissionState.launchMultiplePermissionRequest()
                        },
                        dismiss = {
                            viewModel.deniedLocation()
                        }
                    )
                }

                MapScreenState.Error.MockError -> {
                    ErrorDialog(
                        title = "Mock permission error",
                        message = "You need to set this app as a mock provider in the developer options",
                        onCLick = {TODO()},
                        dismiss = {TODO()}
                    )
                }

                MapScreenState.Error.NotificationError -> {
                    ErrorDialog(
                        title = "Notification permission error",
                        message = "You need to allow the app to post notifications to mock your location. \n You can disable the notification channel if they bother you.",
                        onCLick = {TODO()},
                        dismiss = {TODO()}
                    )
                }
                null -> {}
            }
        }

        LaunchedEffect(locationPermissionState.permissions[0].status.isGranted) {
            if (locationPermissionState.permissions[0].status.isGranted) {
                viewModel.acceptedLocation()
            }
        }
        LaunchedEffect(notificationPermissionState.status.isGranted) {
            if (notificationPermissionState.status.isGranted) {
                viewModel.acceptedLocation()
            }
        }

        //TODO Cleaner
        LaunchedEffect(viewModel.animateToLocation) {
            viewModel.animateToLocation.collect{
                it?.let {
                    cameraState.animate(
                        CameraUpdateFactory.newLatLngZoom(
                            it,13f
                        )
                    )
                }
            }
        }


        Map(
            serviceState = updateLocationService.serviceState.value,
            mapScreenState = viewModel.mapScreenState.value,
            isDarkMode = false,
            cameraState = cameraState,
            markerData = markerData,
            addMarker = { viewModel.addMarker(it) },
            removeMarker = { viewModel.removeMarker(it) },
            clearAllMarkers = { viewModel.clearAllMarkers() },
            polyline = viewModel.polylineState,
            animateCameraToUser = {
                scope.launch {
                    cameraState.animate(
                        CameraUpdateFactory.newLatLngZoom(
                            viewModel.getUserLocation(context), 13f
                        )
                    )
                }
            }
        )
    }
}
//TODO REPLACE WITH MapEffect()


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