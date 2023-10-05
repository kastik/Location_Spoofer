package com.kastik.locationspoofer.ui.componets

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat.startActivity
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale
import com.google.firebase.installations.Utils
import com.google.gson.annotations.Until
import com.kastik.locationspoofer.MainActivity
import com.kastik.locationspoofer.createViewModel
import com.kastik.locationspoofer.data.DatastoreRepo
import com.kastik.locationspoofer.data.MyViewModel
import com.kastik.locationspoofer.ui.DialogTypes
import kotlin.system.exitProcess

@SuppressLint("InlinedApi")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ErrorDialog(
    viewModel: MyViewModel,
    reason: DialogTypes
) {
    val title: String
    val text: @Composable () -> Unit
    val confirmButton: @Composable () -> Unit
    val dismissButton: @Composable () -> Unit
    val context = LocalContext.current



    when(reason) {
        DialogTypes.LocationPermissionError -> {
            title = "The Location Permission is required to do that"
            text = { Text("")}
            confirmButton = {
                TextButton(onClick = {
                    viewModel.locationPermissionState.launchMultiplePermissionRequest()
                }
                ) { Text("Allow") }
            }
            dismissButton =
                { TextButton(onClick = {
                    viewModel.showLocationErrorDialog(false)
                }){ Text("Cancel") } }
        }

        DialogTypes.MockPermissionError -> {
            title = "Please Allow Mock Location Under Developer Options"
            text = {Text("")}
            confirmButton = {
                TextButton(onClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)
                    startActivity(context,intent,null)
                }) { Text("Allow") } }
            dismissButton = {
                TextButton(onClick = {
                    viewModel.showMockPermissionErrorDialog().value = false
                    MainActivity().finish()
                    exitProcess(0)
                }) { Text("Close App") }
            }

        }
        DialogTypes.NotificationPermissionError -> {
            if (viewModel.notificationPermissionState!!.status.shouldShowRationale){
                title = "Please Allow Notifications"
                text = {Text("")}
                confirmButton = {
                    viewModel.notificationPermissionState.launchPermissionRequest()
                }
                dismissButton = {viewModel.showLocationErrorDialog().value = false}
            }else{
                title = "Please Allow Notifications"
                text = {Text("")}
                confirmButton ={
                    TextButton(
                        onClick = {
                            val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
                            putExtra(Settings.EXTRA_APP_PACKAGE, "com.kastik.locationspoofer.debug")
                            putExtra(Settings.EXTRA_CHANNEL_ID, "running_channel")
                        }
                        startActivity(context, intent, null)
                    }){ Text("Allow") }}
                    dismissButton ={ TextButton(onClick = { viewModel.showLocationErrorDialog().value = false
                    }){ Text("Dismiss") }
                    }
            }
        }
        DialogTypes.Introduction ->{
            title = "Hello"
            text = {
                Text("Long Press on the map to select a spot")
                Text("Single Press on a Point of Intrest to select a Spot")
                Text("Single Press anywhere else to unselect the spot")
                Text("Click On the Fab to activate spoofing ")
                Text("You need to activate developer options and select this app as spoof location app")
            }
            confirmButton = {
                TextButton(onClick = {
                    viewModel.locationPermissionState.launchMultiplePermissionRequest()
                }
                ) { Text("Allow") }
            }
            dismissButton =
                { TextButton(onClick = {
                    viewModel.showLocationErrorDialog(false)
                }){ Text("Cancel") } }
        }
    }


    AlertDialog(
        onDismissRequest = { },
        title = { Text(title) },
        text = text ,
        confirmButton = confirmButton,
        dismissButton = dismissButton

    )


}