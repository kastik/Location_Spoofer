package com.kastik.locationspoofer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.libraries.places.api.Places
import com.kastik.locationspoofer.ui.screens.main.UIStuff
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    /*
    private lateinit var mService: UpdateLocationService
    private var mBound: Boolean = false

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance.
            val binder = service as UpdateLocationService.LocalBinder
            mService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

     */

    @ExperimentalPermissionsApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Places.initialize(this, "AIzaSyDmH8eUm3nEYaaqA1q1zx9snrq2c_Mkoao")
        createNotificationChannel()
        setContent {
            MaterialTheme {
                UIStuff()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }


    override fun onStart() {
        super.onStart()
        /*
        Intent(this, UpdateLocationService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
         */
    }

    override fun onStop() {
        super.onStop()
        /*
        unbindService(connection)
        mBound = false
         */
    }

}