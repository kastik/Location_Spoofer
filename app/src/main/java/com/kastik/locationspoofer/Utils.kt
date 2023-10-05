package com.kastik.locationspoofer

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.maps.android.compose.rememberCameraPositionState
import com.kastik.locationspoofer.data.DatastoreRepo
import com.kastik.locationspoofer.data.MyViewModel

fun Context.startSpoofService(viewModel: MyViewModel) {

    if (viewModel.floatingIconStart().value) {
        val myIntent = Intent(this,UpdateLocationService::class.java)
        myIntent.putExtra(
            UpdateLocationService.Coordinates.Latitude.name,
            viewModel.getMarker().value?.latitude
        )
        myIntent.putExtra(
            UpdateLocationService.Coordinates.Longitude.name,
            viewModel.getMarker().value?.longitude
        )
        //IN CASE USER ALT IS NOT NULL OR NULL
        viewModel.getUserAlt().value?.let {
            myIntent.putExtra(UpdateLocationService.Coordinates.Altitude.name, it )
        }?:{
            myIntent.putExtra(UpdateLocationService.Coordinates.Altitude.name, 0.0)
        }

        myIntent.action = UpdateLocationService.ACTIONS.START.name
        viewModel.enableSpoofing()
        startService(myIntent)
    } else {
        val myIntent = Intent(this, UpdateLocationService::class.java)
        myIntent.action = UpdateLocationService.ACTIONS.STOP.name
        viewModel.disableSpoofing()
        startService(myIntent)
    }
}


fun Context.createNotificationChannel() {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is not in the Support Library.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "running_channel",
                "Running Service",
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
}



@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun createViewModel(): MyViewModel{
    return MyViewModel(
        DatastoreRepo.getInstance(LocalContext.current),
        AutocompleteSessionToken.newInstance(),
        rememberMultiplePermissionsState(
            arrayListOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ),
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
            rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
        }else{
            null
        }, rememberCameraPositionState())

}