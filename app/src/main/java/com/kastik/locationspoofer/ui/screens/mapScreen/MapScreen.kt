package com.kastik.locationspoofer.ui.screens.mapScreen

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.LocationSource
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.routing.v2.Route
import com.kastik.locationspoofer.DarkMode
import com.kastik.locationspoofer.SavedPlaces
import com.kastik.locationspoofer.getUserLocation
import com.kastik.locationspoofer.service.LocationMockServiceState
import com.kastik.locationspoofer.ui.screens.mapScreen.components.dialogs.NameInputDialog
import com.kastik.locationspoofer.ui.screens.mapScreen.components.fab.FloatingActionButtons
import com.kastik.locationspoofer.ui.screens.mapScreen.components.map.Map
import com.kastik.locationspoofer.ui.screens.mapScreen.components.searchbar.TopSearchBar
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalPermissionsApi::class, MapsComposeExperimentalApi::class)
@Composable
fun MapScreen(
    navigateToSettings: () -> Unit,
    navigateToSavedRoutes: () -> Unit,
    route: Route? = null
) {
    val viewModel: MapScreenViewModel = hiltViewModel()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val cameraState = rememberCameraPositionState()
    val animateLocationState = viewModel.animateLocationState.collectAsStateWithLifecycle()
    val locationSpoofServiceState = viewModel.serviceManager.serviceState.collectAsStateWithLifecycle()


    val activeMarkersState = viewModel.activeMarkerState


    val savedPlacesState = viewModel.savedPlacesFlow.collectAsState(
        SavedPlaces.getDefaultInstance()
    )

    val locationPermissionState = rememberMultiplePermissionsState(
        arrayListOf(
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
        )
    )

    val notificationPermissionState = rememberPermissionState(
        Manifest.permission.POST_NOTIFICATIONS
    )

    val userPreferences = viewModel.userPreferences.collectAsStateWithLifecycle()

    //Used when navigating to MapScreen with a polyline parameter
    //TODO Cleaner/Safer
    LaunchedEffect(Unit) {
        if (route != null) {
            viewModel.setActiveRoute(route)
            viewModel.startMockingLocation(route)
        }
    }
    //TODO Clean
    LaunchedEffect(Unit) {
        locationPermissionState.launchMultiplePermissionRequest()
        notificationPermissionState.launchPermissionRequest()
    }

    //When emitting a new value to animatedLocationState, update the camera to that location
    LaunchedEffect(animateLocationState.value) {
        animateLocationState.value?.let { latLngBounds ->
            cameraState.animate(
                CameraUpdateFactory.newLatLngBounds(
                    latLngBounds, 0
                )
            )
        }
    }

    Scaffold(topBar = {
        TopSearchBar(
            navigateToSettings = navigateToSettings,
            searchForPlace = { query -> viewModel.searchForPlace(query) },
            moveCameraToResultId = { id -> viewModel.moveCameraToResultId(id) },
            searchResults = viewModel.searchResultsState,
            savedPlacesList = savedPlacesState.value,
        )
    }, floatingActionButton = {
        FloatingActionButtons(
            navigateToSavedRoutes = navigateToSavedRoutes,
            isSpoofing = locationSpoofServiceState.value is LocationMockServiceState.MockingLocation,
            savePlace = { viewModel.openSavePlaceDialog() },
            unSavePlace = { viewModel.deletePlace() },
            isPlaceSaved = viewModel.isActiveMarkerOnSavedPlaceState.value, //TODO Clean
            showSaveButton = viewModel.showSaveButton,
            hasPlacedPolylineOrMarker = viewModel.hasPlacedMarkerOrPolyline,
            stopSpoofing = { viewModel.stopSpoofing() },
            startSpoofing = { viewModel.startMockingLocation() },
            moveCameraToUser = {
                scope.launch {
                    getUserLocation(context)?.let {
                        cameraState.animate(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    it.latitude, it.longitude
                                ), 13f
                            )
                        )
                    }
                }
            },
        )
    }) { innerPadding ->

        AnimatedVisibility(
            locationSpoofServiceState.value is LocationMockServiceState.Failed || viewModel.errorDialogState.value != null
        ) {
            //TODO Unify these
            locationSpoofServiceState.value.let {
                when (it) {
                    is LocationMockServiceState.Failed -> {
                        Text(text = it.error)
                    }

                    else -> {}
                }
            }
        }

        AnimatedVisibility(viewModel.showPointNameDialogState.value) {
            NameInputDialog(
                title = "Enter location name",
                firstLabel = "Location",
                firstValue = viewModel.originName.value,
                onFirstValueChange = { viewModel.setOriginName(it) },
                onDismiss = { viewModel.togglePointNameDialog(false) },
                onConfirm = {
                    viewModel.togglePointNameDialog(false)
                    viewModel.saveActivePlaceOrRoute()
                })
        }
        AnimatedVisibility(viewModel.showOriginAndDestinationNameDialogState.value) {
            NameInputDialog(
                title = "Enter origin and destination names",
                firstLabel = "Origin name",
                firstValue = viewModel.originName.value,
                onFirstValueChange = { viewModel.setOriginName(it) },
                secondLabel = "Destination name",
                secondValue = viewModel.destinationName.value,
                onSecondValueChange = { viewModel.setDestinationName(it) },
                onDismiss = { viewModel.toggleOriginAndDestinationNameDialog(false) },
                onConfirm = {
                    viewModel.toggleOriginAndDestinationNameDialog(false)
                    viewModel.saveActivePlaceOrRoute()
                })
        }

        Map(
            isDarkMode = when (userPreferences.value.darkMode) {
                DarkMode.darkMode -> true
                DarkMode.lightMode -> false
                else -> isSystemInDarkTheme()
            },
            cameraState = cameraState,
            markerData = activeMarkersState,
            addMarker = { viewModel.addMarker(it) },
            removeMarker = { viewModel.removeMarker(it) },
            clearAllMarkers = { viewModel.clearAllMarkers() },
            route = viewModel.activeRouteState.value,
            hasLocationEnabled = locationPermissionState.allPermissionsGranted,
            spoofingLocation = when (locationSpoofServiceState.value) {
                is LocationMockServiceState.MockingLocation -> (locationSpoofServiceState.value as LocationMockServiceState.MockingLocation).currentLatLng
                else -> null
            },
            animateCameraToUser = {
                scope.launch {
                    getUserLocation(context)?.let {
                        cameraState.animate(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(it.latitude, it.longitude), 13f
                            )
                        )
                    }
                }
            })
    }
}

class SpoofedLocationSource(
    val spoofedLocation: LatLng
) : LocationSource {
    override fun activate(p0: LocationSource.OnLocationChangedListener) {
        Log.d("MyLog", "SpoofedLocationSource activate")
        val location = Location(LocationManager.GPS_PROVIDER)
        location.latitude = spoofedLocation.latitude
        location.longitude = spoofedLocation.longitude
        p0.onLocationChanged(location)
    }

    override fun deactivate() {
        //Do i need a TODO here?
    }
}