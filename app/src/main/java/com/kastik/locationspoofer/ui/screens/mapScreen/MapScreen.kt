package com.kastik.locationspoofer.ui.screens.mapScreen

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.LocationSource
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.rememberCameraPositionState
import com.kastik.locationspoofer.DarkMode
import com.kastik.locationspoofer.SavedPlaces
import com.kastik.locationspoofer.SavedRoutes
import com.kastik.locationspoofer.getUserLocation
import com.kastik.locationspoofer.service.UpdateLocationService
import com.kastik.locationspoofer.ui.screens.AvailableScreens
import com.kastik.locationspoofer.ui.screens.mapScreen.components.dialogs.NameInputDialog
import com.kastik.locationspoofer.ui.screens.mapScreen.components.fab.FloatingActionButtons
import com.kastik.locationspoofer.ui.screens.mapScreen.components.map.Map
import com.kastik.locationspoofer.ui.screens.mapScreen.components.searchbar.TopSearchBar
import kotlinx.coroutines.launch


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalPermissionsApi::class, MapsComposeExperimentalApi::class)
@Composable
fun MapScreen(
    updateLocationService: UpdateLocationService,
    navigate: (String) -> Unit,
    polyline: String? = null
) {
    val viewModel: MapScreenViewModel = hiltViewModel()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val cameraState = rememberCameraPositionState()
    val animatedLocationState = viewModel.animatedLocationState.collectAsStateWithLifecycle()


    viewModel.setServiceState(updateLocationService.serviceState.value)
    val markerData = viewModel.activeMarkerState


    val savedPlacesState = viewModel.savedPlacesFlow.collectAsState(
        SavedPlaces.getDefaultInstance()
    )

    val savedRoutesState = viewModel.savedRoutesFlow.collectAsState(
        SavedRoutes.getDefaultInstance()
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
    LaunchedEffect(Unit) {
        if (polyline != null) {
            viewModel.setPolyline(polyline)
            viewModel.startMockingLocation(
                binder = updateLocationService,
                context = context,
            )
        }
    }

    //When emitting a new value to animatedLocationState, update the camera to that location
    LaunchedEffect(animatedLocationState.value) {
        animatedLocationState.value?.let {
            cameraState.animate(
                CameraUpdateFactory.newLatLngBounds(
                    it,0
                )
            )
        }
    }


    Scaffold(topBar = {
        TopSearchBar(
            navigateToSettings = { navigate(AvailableScreens.SettingsScreen.name) },
            searchPlaces = { query -> viewModel.searchForPlace(query) },
            moveToPlaceWithId = { id,zoom ->
                viewModel.moveToPlaceWithId(id)
                //viewModel.zoomCamera(zoom)
                },
            placeResults = viewModel.searchResultsState,
            savedPlacesState = savedPlacesState.value,
            savedRoutesState = savedRoutesState.value
        )
    }, floatingActionButton = {
        FloatingActionButtons(
            showSavedPlaces = userPreferences.value.enableStatusBarSavedRoutes,
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
            savePlace = { viewModel.openSavePlaceDialog() },
            showSaveButton = viewModel.showSaveButton,
            removeSavedPlace = { viewModel.deletePlace() },
            isPlaceSaved = viewModel.isActiveMarkerOnSavedPlaceState.value,
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
                viewModel.startMockingLocation(
                    binder = updateLocationService,
                    context = context,
                )

            },
            hasPlacedPolylineOrMarker = viewModel.hasPlacedMarkerOrPolyline,
            serviceState = updateLocationService.serviceState.value,
            navigateToSavedRoutesScreen = {
                navigate(AvailableScreens.SavedRoutesScreen.name)
            })
    }) { innerPadding ->

        AnimatedVisibility(viewModel.errorDialogState.value != null) {
            //TODO Error dialog
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
            serviceState = updateLocationService.serviceState.value,
            isDarkMode = when (userPreferences.value.darkMode) {
                DarkMode.darkMode -> true
                DarkMode.lightMode -> false
                else -> isSystemInDarkTheme()
            },
            cameraState = cameraState,
            markerData = markerData,
            addMarker = { viewModel.addMarker(it) },
            removeMarker = { viewModel.removeMarker(it) },
            clearAllMarkers = { viewModel.clearAllMarkers() },
            route = viewModel.activeRouteState.value,
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
        val location = Location(LocationManager.GPS_PROVIDER)
        location.latitude = spoofedLocation.latitude
        location.longitude = spoofedLocation.longitude
        p0.onLocationChanged(location)
    }

    override fun deactivate() {
        //Do i need a TODO here?
    }
}