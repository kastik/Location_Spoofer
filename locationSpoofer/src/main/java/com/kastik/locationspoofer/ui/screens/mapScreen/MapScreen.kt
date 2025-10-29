package com.kastik.locationspoofer.ui.screens.mapScreen

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.kastik.locationspoofer.DarkMode
import com.kastik.locationspoofer.data.mapers.toLatLng
import com.kastik.locationspoofer.domain.model.PlaceDomain
import com.kastik.locationspoofer.domain.model.RouteDomain
import com.kastik.locationspoofer.getUserLocation
import com.kastik.locationspoofer.ui.components.DeleteDialog
import com.kastik.locationspoofer.ui.components.DialogState
import com.kastik.locationspoofer.ui.components.SaveDialog
import com.kastik.locationspoofer.ui.screens.mapScreen.components.dialogs.ErrorDialog
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
    route: RouteDomain? = null,
    place: PlaceDomain? = null
) {
    val viewModel: MapScreenViewModel = hiltViewModel()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val cameraState = rememberCameraPositionState()
    val uiState = viewModel.uiState

    val locationPermissionState = rememberMultiplePermissionsState(
        arrayListOf(
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
        )
    )

    val notificationPermissionState = rememberPermissionState(
        Manifest.permission.POST_NOTIFICATIONS
    )

    val userPreferences = viewModel.userPreferences.collectAsStateWithLifecycle()

    LaunchedEffect(place, route) {
        viewModel.handleIncomingArgs(context,place, route)
    }
    //TODO Clean
    LaunchedEffect(Unit) {
        locationPermissionState.launchMultiplePermissionRequest()
        notificationPermissionState.launchPermissionRequest()
    }
    LaunchedEffect(uiState.animateCameraTarget) {
        when (val target = uiState.animateCameraTarget) {
            is CameraTarget.Bounds -> {
                cameraState.animate(CameraUpdateFactory.newLatLngBounds(target.bounds, 80))
                viewModel.clearCameraTarget()
            }

            is CameraTarget.Point -> {
                cameraState.animate(CameraUpdateFactory.newLatLngZoom(target.latLng, 15f))
                viewModel.clearCameraTarget()
            }

            CameraTarget.None -> Unit
        }
    }


    Scaffold(
        topBar = {
            TopSearchBar(
                navigateToSettings = navigateToSettings,
                onSearchQueryChanged = { query -> viewModel.searchForPlace(query) },
                onChipClicked = { latLng -> viewModel.animateTo(CameraTarget.Point(latLng.toLatLng())) },
                searchResults = uiState.searchResults,
                savedPlaces = if (userPreferences.value.enableStatusBarSavedRoutes) uiState.savedPlaces else emptyList(),
                onSearchResultClick = { viewModel.moveCameraToResult(it) }, //TODO
            )
        }, floatingActionButton = {
            FloatingActionButtons(
                navigateToSavedRoutes = navigateToSavedRoutes,
                isSpoofing = uiState.fabState.isSpoofing,
                savePlace = viewModel::openSaveDialog,
                unSavePlace = viewModel::openDeleteDialog,
                isPlaceSaved = uiState.fabState.isActiveSaved,
                showSaveButton = uiState.fabState.showSaveButton,
                showSpoofButton = uiState.fabState.showSpoofButton,
                stopSpoofing = {viewModel.stopSpoofing(context) },
                startSpoofing = { viewModel.startSpoofing(context) },
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
            uiState.error != null,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            ErrorDialog(
                title = uiState.error?.title ?: "",
                message = uiState.error?.message,
                onCLick = { uiState.error?.action },
                dismiss = { uiState.error?.dismiss }
            )
        }

        AnimatedVisibility(
            visible = uiState.dialogState is DialogState.SavePlace,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            val dialog = uiState.dialogState as? DialogState.SavePlace
            if (dialog != null) {
                SaveDialog(
                    title = "Saving location",
                    firstLabel = "Location",
                    initialFirstValue = dialog.place.name ?: "",
                    onDismiss = viewModel::dismissDialogs,
                    onConfirm = { newName ->
                        viewModel.confirmSaveDialog(newName)
                    }
                )
            }
        }


        AnimatedVisibility(visible = uiState.dialogState is DialogState.SaveRoute) {
            val dialog = uiState.dialogState as? DialogState.SaveRoute
            if (dialog != null) {
                SaveDialog(
                    title = "Enter origin and destination names",
                    firstLabel = "Origin name",
                    initialFirstValue = dialog.route.origin.orEmpty(),
                    secondLabel = "Destination name",
                    initialSecondValue = dialog.route.destination.orEmpty(),
                    onDismiss = viewModel::dismissDialogs,
                    onConfirm = { newOriginName, newDestinationName ->
                        viewModel.confirmSaveDialog(newOriginName, newDestinationName)
                    }
                )
            }
        }


        AnimatedVisibility(visible = uiState.dialogState is DialogState.DeleteRoute) {
            val dialog = uiState.dialogState as? DialogState.DeleteRoute
            if (dialog != null) {
                DeleteDialog(
                    route = dialog.route,
                    onConfirm = viewModel::deleteRoute,
                    onDismiss = viewModel::dismissDialogs
                )
            }
        }

        AnimatedVisibility(visible = uiState.dialogState is DialogState.DeletePlace) {
            val dialog = uiState.dialogState as? DialogState.DeletePlace
            if (dialog != null) {
                DeleteDialog(
                    place = dialog.place,
                    onConfirm = viewModel::deletePlace,
                    onDismiss = viewModel::dismissDialogs
                )
            }
        }


        Map(
            isDarkMode = when (userPreferences.value.darkMode) {
                DarkMode.darkMode -> true
                DarkMode.lightMode -> false
                else -> isSystemInDarkTheme()
            },
            cameraState = cameraState,
            placedMarkers = uiState.activePlaces.map { it.location },
            addMarker = viewModel::addMarker,
            removeMarker = { viewModel.removeMarker(it) },
            clearAllMarkers = viewModel::clearMarkersAndRoute,
            route = uiState.activeRoute,
            hasLocationEnabled = locationPermissionState.allPermissionsGranted,
            spoofingLocation = when (val state = uiState.spoofState) {
                is SpoofState.Spoofing -> state.latLngDomain.toLatLng()
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