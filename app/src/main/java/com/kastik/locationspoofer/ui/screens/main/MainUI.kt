package com.kastik.locationspoofer.ui.screens.main

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.rememberCameraPositionState
import com.kastik.locationspoofer.ServiceStateTracker
import com.kastik.locationspoofer.data.models.ServiceState
import com.kastik.locationspoofer.debug.SavedPlaces
import com.kastik.locationspoofer.ui.screens.main.components.fab.FloatingActionButtons
import com.kastik.locationspoofer.ui.screens.main.components.searchbar.TopSearchBar
import com.kastik.locationspoofer.ui.screens.AvailableScreens
import com.kastik.locationspoofer.ui.screens.main.components.map.MapScreen
import com.kastik.locationspoofer.ui.screens.main.components.map.MapScreenState
import com.kastik.locationspoofer.ui.screens.main.components.dialogs.ErrorDialog
import com.kastik.locationspoofer.ui.screens.settingsScreen.SettingsScreen
import com.kastik.locationspoofer.ui.theme.LocationSpooferTheme
import kotlinx.coroutines.launch

//todo when clicking mock without mock provier the locate me fab disappears
//todo dismiss popups after onClick

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("MissingPermission", "UnusedMaterial3ScaffoldPaddingParameter",
    "StateFlowValueCalledInComposition"
)
@Composable
@ExperimentalPermissionsApi
fun UIStuff(
    viewModel: MainUIViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val cameraState: CameraPositionState = rememberCameraPositionState()


    val savedPlacesState = viewModel.savedPlacesFlow.collectAsState(
        SavedPlaces.newBuilder().build()
    )

    val hasLocationPermissions = rememberMultiplePermissionsState(
        arrayListOf(
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
        )
    )

    val notificationPermissionState = rememberPermissionState(
        Manifest.permission.POST_NOTIFICATIONS
    )

    LaunchedEffect(Unit) {
        viewModel.animateToLocation.collect { location ->
            location?.let {
                cameraState.animate(
                    CameraUpdateFactory.newLatLngZoom(it, 13f)
                )
            }
        }
    }


    //Check if sevice already running in the background (in case of onResume, re-compositions, etc)
    LaunchedEffect(
        ServiceStateTracker.serviceState, hasLocationPermissions.permissions[0].status.isGranted
    ) {
        val serviceState = ServiceStateTracker.serviceState.value
        when (serviceState) {
            is ServiceState.Running -> {
                viewModel.updateMapScreenState(MapScreenState.SpoofingLocation(serviceState.spoofLocation))
            }
            ServiceState.Stopped -> {
                viewModel.getInitialState(
                    context = context, locationPermissionState = hasLocationPermissions
                )
            }
        }
    }

    val isInMapScreen =
        navController.currentBackStackEntryAsState().value?.destination?.route == AvailableScreens.MapScreen.name


    ErrorDialog(
        mapScreenState = viewModel.mapScreenState.value, dismiss = {
            //TODO Cleaner
            viewModel.updateMapScreenState(MapScreenState.NoLocation)
        })
    LocationSpooferTheme {
        Scaffold(topBar = {
            TopSearchBar(
                visible = isInMapScreen,
                navigateToSettings = { navController.navigate(AvailableScreens.Settings.name) },
                savedPlaces = savedPlacesState.value,
                moveCamera = {
                    viewModel.moveCamera(
                        LatLng(
                            it.latitude, it.longitude
                        )
                    )
                },
                changeMarker = {viewModel.changeMarker(it)}
            )

        }, floatingActionButton = {
            FloatingActionButtons(
                marker = viewModel.markerState.value,
                mapScreenState = viewModel.mapScreenState.value,
                moveCameraTo = {
                    viewModel.moveCamera(it)
                },
                savePlace = {
                    viewModel.savePlace(it)

                },
                removeSavedPlace = {viewModel.deletePlace()},
                isPlaceSaved = viewModel.isPlaceSaved.collectAsState(false),
                stopSpoofing = {
                    scope.launch {
                        viewModel.stopSpoofing(
                            context, hasLocationPermissions
                        )
                    }
                },
                startSpoofing = {
                    viewModel.startMockingLocation(
                        context, hasLocationPermissions,notificationPermissionState
                    )

                }

            )
        }) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = AvailableScreens.MapScreen.name,
                modifier = Modifier.consumeWindowInsets(innerPadding)
            ) {
                composable(AvailableScreens.MapScreen.name) {
                    MapScreen(
                        markerData = viewModel.markerState.value,
                        setMarkerData = { viewModel.changeMarker(it) },
                        cameraState = cameraState,
                        locationPermissionState = hasLocationPermissions,
                        mapScreenState = viewModel.mapScreenState.value
                    )
                }
                composable(AvailableScreens.Settings.name) {
                    SettingsScreen()
                }
            }
        }
    }
}