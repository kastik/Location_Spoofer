package com.kastik.locationspoofer

import android.content.ComponentName
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.libraries.places.api.Places
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.kastik.locationspoofer.data.datastore.UserPreferencesRepo
import com.kastik.locationspoofer.service.LocationMockServiceState
import com.kastik.locationspoofer.service.UpdateLocationService
import com.kastik.locationspoofer.ui.screens.main.UIStuff
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var userPreferencesRepo: UserPreferencesRepo

    private lateinit var locationService: UpdateLocationService
    private val _isBound = mutableStateOf(false)
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as UpdateLocationService.LocalBinder
            locationService = binder.getService()
            _isBound.value = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            _isBound.value = false
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @ExperimentalPermissionsApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAnalytics = Firebase.analytics

        createNotificationChannel() // create channel first
        val serviceIntent = Intent(this, UpdateLocationService::class.java)
        startService(serviceIntent) // started service

        setContent {
            if (_isBound.value) {
                UIStuff(locationService)
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val serviceIntent = Intent(this, UpdateLocationService::class.java)
        bindService(serviceIntent, connection, BIND_AUTO_CREATE) // bind service
    }

    override fun onStop() {
        super.onStop()
        if (_isBound.value) {
            unbindService(connection)
            _isBound.value = false
        }

        if (this::locationService.isInitialized &&
            locationService.serviceState.value is LocationMockServiceState.Idle
        ) {
            stopService(Intent(this, UpdateLocationService::class.java))
        }
    }
}
