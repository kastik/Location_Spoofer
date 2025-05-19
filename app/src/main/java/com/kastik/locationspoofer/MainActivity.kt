package com.kastik.locationspoofer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.kastik.locationspoofer.ui.screens.main.UIStuff
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var mService: UpdateLocationService
    private val _isBound = mutableStateOf(false)

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as UpdateLocationService.LocalBinder
            mService = binder.getService()
            _isBound.value = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            _isBound.value = false
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @ExperimentalPermissionsApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Places.initialize(this, TODO())
        startService(Intent(this, UpdateLocationService::class.java))


        createNotificationChannel()
        setContent {
            MaterialTheme {
                if (_isBound.value) {
                    UIStuff(mService)
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onStop() {
        super.onStop()
        if (mService.serviceState.value is LocationMockServiceState.Idle) {
            //TODO stopService()
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, UpdateLocationService::class.java).also { intent ->
            bindService(intent, connection, BIND_AUTO_CREATE)
        }
    }
}

@Composable
fun LoadingScreen() {
    androidx.compose.material3.Text("Connecting to service...")
}