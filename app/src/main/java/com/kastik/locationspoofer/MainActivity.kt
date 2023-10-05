package com.kastik.locationspoofer

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.preferencesDataStore
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.kastik.locationspoofer.data.DatastoreRepo
import com.kastik.locationspoofer.data.MyViewModel
import com.kastik.locationspoofer.ui.UIStuff


private val Context.dataStore by preferencesDataStore("settings")

class MainActivity : ComponentActivity() {

    private  lateinit var serviceBroadcastReceiver: BroadcastReceiver
    private lateinit var viewModel: MyViewModel

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @ExperimentalPermissionsApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Places.initialize(this, "AIzaSyDTbMB1nHmiE_uGnbB15yaQ6-PJaTQvD9c")
        createNotificationChannel()

        setContent {
            viewModel = createViewModel()
            serviceBroadcastReceiver = ServiceBroadcastReceiver(viewModel)
            val intentFilter = IntentFilter("MOCK")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(serviceBroadcastReceiver, intentFilter, RECEIVER_NOT_EXPORTED)
            }else{
                registerReceiver(serviceBroadcastReceiver,intentFilter)
            }

            UIStuff(viewModel)



        }




    }




    override fun onDestroy() {
        unregisterReceiver(serviceBroadcastReceiver)
        super.onDestroy()
    }

}



private class ServiceBroadcastReceiver(val viewModel: MyViewModel): BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val success = intent.getBooleanExtra("SUCCESS",false)
        if (success){
            viewModel.enableSpoofing()
        }else{
            viewModel.disableSpoofing()
            viewModel.showMockPermissionErrorDialog(true)
        }
    }
}





