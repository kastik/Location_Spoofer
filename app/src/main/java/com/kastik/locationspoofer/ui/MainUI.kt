package com.kastik.locationspoofer.ui

import android.annotation.SuppressLint
import android.location.Location
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.kastik.locationspoofer.data.MyViewModel
import com.kastik.locationspoofer.startSpoofService
import com.kastik.locationspoofer.ui.componets.ErrorDialog
import com.kastik.locationspoofer.ui.componets.TopSearchBar
import com.kastik.locationspoofer.ui.screens.AvailableScreens
import com.kastik.locationspoofer.ui.screens.MapScreen
import com.kastik.locationspoofer.ui.screens.SettingsScreen
import com.kastik.locationspoofer.ui.theme.LocationSpooferTheme
import kotlinx.coroutines.launch


@SuppressLint("MissingPermission", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
@ExperimentalPermissionsApi
fun UIStuff(viewModel: MyViewModel) {
    val context = LocalContext.current
    val placesClient = Places.createClient(LocalContext.current)
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()


    LaunchedEffect(viewModel.locationPermissionState.permissions[0].status.isGranted) {
        if (!viewModel.locationPermissionState.permissions[0].status.isGranted) {
            viewModel.locationPermissionState.launchMultiplePermissionRequest()
            viewModel.setUserLatLng(LatLng(0.0, 0.0))
        } else {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        viewModel.setUserLatLng(LatLng(location.latitude, location.longitude))
                        viewModel.setUserAlt(location.altitude)
                        val cameraState =  viewModel.cameraState
                        scope.launch {
                            cameraState.animate(
                                CameraUpdateFactory.newLatLng(
                                    LatLng(location.latitude, location.longitude)
                                )
                            )
                            cameraState.animate(CameraUpdateFactory.zoomTo(13f))
                        }
                    }
                }
                    //TODO
        }
    }

viewModel.notificationPermissionState?.let {
        LaunchedEffect(viewModel.notificationPermissionState.status) {
            if (!viewModel.notificationPermissionState.status.isGranted) {
                viewModel.notificationPermissionState.launchPermissionRequest()
            } else {
                if (viewModel.notificationPermissionState.status.shouldShowRationale) {
                    viewModel.showNotificationPermissionErrorDialog(true)
                } else {
                    //TODO
                }
            }
        }
    }



    LocationSpooferTheme {
        Scaffold(
            topBar = {
                AnimatedVisibility(
                    viewModel.isInMapScreen().value,
                    enter = slideInVertically(), exit = slideOutVertically()
                ) {
                    TopSearchBar(viewModel, navController, placesClient)
                }
            },
            floatingActionButton = {
                AnimatedVisibility(
                    viewModel.isInMapScreen().value && viewModel.showFab().value,
                    enter = scaleIn(), exit = scaleOut()
                ) {
                    MapFloatingButton(viewModel)
                }
            })
        {
            Navigation(navController, viewModel)
        }
    }
}


@Composable
private fun MapFloatingButton(viewModel: MyViewModel) {
    val context = LocalContext.current
    FloatingActionButton(
        onClick = { context.startSpoofService(viewModel) },
        content = {
            if (viewModel.floatingIconStart().value) {
                Icon(Icons.Default.LocationOn, "Apply Spoof")
            } else {
                Icon(Icons.Default.Close, "Remove Spoof")
            }
        })
}


enum class DialogTypes {
    LocationPermissionError, MockPermissionError, NotificationPermissionError, Introduction
}


@Composable
private fun Navigation(navController: NavHostController, viewModel: MyViewModel) {
    NavHost(
        navController = navController,
        startDestination = AvailableScreens.Map.name,
        //modifier = Modifier.padding(paddingValues)
    ) {
        composable(AvailableScreens.Map.name) {
            viewModel.isInMapScreen().value = true
            if (viewModel.showLocationErrorDialog().value) {
                ErrorDialog(viewModel, DialogTypes.LocationPermissionError)
            }

            if ((viewModel.showMockPermissionErrorDialog().value)) {
                ErrorDialog(viewModel, DialogTypes.MockPermissionError)
            }

            if (viewModel.showNotificationPermissionErrorDialog().value){
                ErrorDialog(viewModel,DialogTypes.NotificationPermissionError)
            }
            MapScreen(viewModel)

        }
        composable(AvailableScreens.Settings.name) {
            viewModel.isInMapScreen().value = false
            SettingsScreen(viewModel)
        }
    }
}

