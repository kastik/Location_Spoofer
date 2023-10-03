package com.kastik.locationspoofer.ui.screens

import android.annotation.SuppressLint
import android.location.Location
import android.media.audiofx.Virtualizer.Settings
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.kastik.locationspoofer.MainActivity
import com.kastik.locationspoofer.data.MyViewModel
import com.kastik.locationspoofer.startSpoofService
import com.kastik.locationspoofer.ui.componets.TopSearchBar
import com.kastik.locationspoofer.ui.theme.LocationSpooferTheme
import kotlin.system.exitProcess


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "MissingPermission")
@Composable
@ExperimentalPermissionsApi
fun UIStuff(viewModel: MyViewModel) {
    val fusedLocationClient: FusedLocationProviderClient

    Places.initialize(LocalContext.current, "AIzaSyDTbMB1nHmiE_uGnbB15yaQ6-PJaTQvD9c")
    val placesClient = Places.createClient(LocalContext.current)
    val navController = rememberNavController()
    val permissionState = checkPermissions()


    if (permissionState.allPermissionsGranted) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(LocalContext.current)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location ->
                viewModel.setUserLatLng(LatLng(location.latitude,location.longitude))
                viewModel.setLocationPermissionGranted(true)
            } }
    else {
        viewModel.setUserLatLng(LatLng(0.0,0.0))
        viewModel.setLocationPermissionGranted(true)
    }

    LocationSpooferTheme {
        Scaffold(
            topBar = { TopSearchBar(viewModel, navController, placesClient) },
            floatingActionButton = { MapFloatingButton(viewModel) }
        ) {
            Navigation(navController,viewModel)
        }
    }
}


@Composable
fun MapFloatingButton(viewModel: MyViewModel) {
    val context = LocalContext.current
    AnimatedVisibility(true) {
        FloatingActionButton(
            onClick = { startSpoofService(viewModel, context) },
            content = {
                if (viewModel.floatingIconStart().value) {
                    Icon(Icons.Default.LocationOn, "Apply Spoof")
                } else {
                    Icon(Icons.Default.Close, "Remove Spoof")
                }
            })
    }
}

@Composable
@ExperimentalPermissionsApi
fun checkPermissions(): MultiplePermissionsState {
    val permissionState =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            rememberMultiplePermissionsState(
                arrayListOf(
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.POST_NOTIFICATIONS
                )
            )
        } else {
            rememberMultiplePermissionsState(
                arrayListOf(
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }
    return permissionState
}




enum class DialogTypes{
    LocationError,MockPermissionError
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ShowDialog(
    viewModel: MyViewModel,
    reason: DialogTypes
) {

    val permissionState = checkPermissions()

    val title: String
    val text: String
    val confirmButton: @Composable () -> Unit
    val dismissButton: @Composable () -> Unit
    if (reason == DialogTypes.LocationError){

        title = "The Location Permission is required to do that"
        text = ""
        confirmButton= { TextButton(onClick = {
            permissionState.launchMultiplePermissionRequest()
        }
        ) { Text("Grand") } }
        dismissButton = { TextButton(onClick = { viewModel.showLocationErrorDialog(false) }) { Text("Cancel") } }

    }else{
            title = "Please Allow Mock Location Under Developer Options"
            text = ""
            confirmButton= { TextButton(onClick = { viewModel.showMockPermissionErrorDialog().value }) { Text("Grand") } }
            dismissButton = { TextButton(onClick = {
                viewModel.showMockPermissionErrorDialog().value = false
                MainActivity().finish()
                exitProcess(0)
            }) { Text("Cancel") } }

    }


    AlertDialog(
        onDismissRequest = {   },
        title = { Text(title) },
        text = { Text(text) },
        confirmButton =  confirmButton,
        dismissButton = dismissButton

    )


}

@Composable
fun Navigation(navController: NavHostController,viewModel: MyViewModel){
    NavHost(
        navController = navController,
        startDestination = AvailableScreens.Map.name,
        //modifier = Modifier.padding(it)
    ) {
        composable(AvailableScreens.Map.name) {
                if(viewModel.showLocationErrorDialog().value) {
                    ShowDialog(viewModel,DialogTypes.LocationError)
                }

                    if ((viewModel.showMockPermissionErrorDialog().value)) {
                        ShowDialog(viewModel,DialogTypes.MockPermissionError)
                    }
                    MapScreen(viewModel)

        }
        composable(AvailableScreens.Settings.name) {
            Settings()
        }
    }
}

