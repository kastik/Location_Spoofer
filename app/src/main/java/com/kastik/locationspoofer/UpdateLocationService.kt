package com.kastik.locationspoofer

import android.app.Service
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.location.provider.ProviderProperties
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.maps.model.LatLng
import com.kastik.locationspoofer.data.models.ServiceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.concurrent.thread

//TODO Re-Implement binder to add IPC

class UpdateLocationService : Service() {
    private val mainHandler = Handler(Looper.getMainLooper())
    private val binder = LocalBinder()
    private lateinit var runnableJob: Runnable
    private lateinit var broadIntent: Intent

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val latLng = LatLng(
            intent.getDoubleExtra("Latitude", 0.0),
            intent.getDoubleExtra("Longitude", 0.0)
        )
        Log.d("MyLog","LATLNG IS $latLng")
        ServiceStateTracker.setServiceRunning(true,latLng)
        createNotification()
        thread {
            setLocation(latLng)
        }
        return START_STICKY
    }

    private fun createNotification() {
        val notification =
            NotificationCompat.Builder(this, "running_channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("My notification")
                .setContentText("Much longer text that cannot fit one line...")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()
        startForeground(1, notification)
    }

    private fun setLocation(latLng: LatLng) {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        if (this::runnableJob.isInitialized) {
            mainHandler.removeCallbacks(runnableJob)
        }
        locationManager.addTestProvider(
            LocationManager.GPS_PROVIDER,
            false,
            false,
            false,
            false,
            true,
            true,
            true,
            ProviderProperties.POWER_USAGE_LOW,
            ProviderProperties.ACCURACY_FINE
        )

        val location = Location(LocationManager.GPS_PROVIDER)

        runnableJob = object : Runnable {
            override fun run() {
                location.latitude = latLng.latitude
                location.longitude = latLng.longitude
                location.time = System.currentTimeMillis()
                location.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
                location.setAccuracy(5F)
                locationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, location)
                mainHandler.postDelayed(this, 100)
            }
        }
        locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true)
        mainHandler.post(runnableJob)
        broadIntent = Intent("MOCK")
        broadIntent.putExtra("SUCCESS", true)
        sendBroadcast(broadIntent)

    }

    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods.
        fun getService(): UpdateLocationService = this@UpdateLocationService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onDestroy() {
        mainHandler.removeCallbacks(runnableJob)
        ServiceStateTracker.setServiceRunning(false,null)
        super.onDestroy()
    }

}

object ServiceStateTracker {
    private val _serviceMutableStateFlow = MutableStateFlow<ServiceState>(ServiceState.Stopped)
    val serviceState: StateFlow<ServiceState> = _serviceMutableStateFlow.asStateFlow()

    fun setServiceRunning(running: Boolean, latLng: LatLng?) {
        _serviceMutableStateFlow.value = if (running && latLng != null) {
            ServiceState.Running(latLng)
        } else {
            ServiceState.Stopped
        }
    }
}
