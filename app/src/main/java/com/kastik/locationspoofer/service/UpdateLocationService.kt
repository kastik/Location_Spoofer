package com.kastik.locationspoofer.service

import android.app.Service
import android.content.Intent
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.location.provider.ProviderProperties
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat
import com.google.android.gms.maps.model.LatLng
import com.google.maps.routing.v2.Polyline
import com.google.maps.routing.v2.Route
import com.kastik.locationspoofer.R
import com.kastik.locationspoofer.data.models.decodeToLatLngList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class UpdateLocationService : Service() {
    private val _serviceStateFlow = MutableStateFlow<LocationMockServiceState>(LocationMockServiceState.Idle)
    val serviceStateFlow: StateFlow<LocationMockServiceState> = _serviceStateFlow
    private var locationJob: Job? = null
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val binder = LocalBinder()
    private var currentSpeedMps: Float = 16.666666f
    private val updateIntervalMs = 1000L

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d("MyLog", "onstart")
        _serviceStateFlow.value = LocationMockServiceState.Idle
        return START_STICKY
    }

    fun startMockingLocation(route: Route) =
        startMockingLocation(route.polyline.decodeToLatLngList())

    fun startMockingLocation(polyline: Polyline) =
        startMockingLocation(polyline.decodeToLatLngList())

    fun startMockingLocation(location: LatLng) = startMockingLocation(listOf(location))

    @Suppress("DEPRECATION", "WrongConstant")
    private fun startMockingLocation(
        path: List<LatLng>, loop: Boolean = false
    ) {
        if (path.isEmpty()) return
        createNotification()
        locationJob?.cancel()
        locationJob = serviceScope.launch {
            runCatching {
                val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    locationManager.removeTestProvider(LocationManager.GPS_PROVIDER)
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
                } else {
                    locationManager.addTestProvider(
                        LocationManager.GPS_PROVIDER,
                        false,
                        false,
                        false,
                        false,
                        true,
                        true,
                        true,
                        Criteria.POWER_LOW,
                        Criteria.ACCURACY_FINE
                    )
                }
                locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true)

                var currentIndex = 0
                var currentPoint = path[0]

                while (isActive) {
                    val nextIndex = currentIndex + 1

                    if (nextIndex >= path.size) {
                        if (loop) {
                            currentIndex = 0
                            currentPoint = path[0]
                            continue
                        } else {
                            break
                        }
                    }

                    val nextPoint = path[nextIndex]

                    val results = FloatArray(1)
                    Location.distanceBetween(
                        currentPoint.latitude,
                        currentPoint.longitude,
                        nextPoint.latitude,
                        nextPoint.longitude,
                        results
                    )
                    val segmentDistance = results[0]

                    val distanceToTravel = currentSpeedMps * (updateIntervalMs / 1000f)

                    currentPoint = if (segmentDistance <= distanceToTravel) {
                        currentIndex++
                        nextPoint
                    } else {
                        val fraction = distanceToTravel / segmentDistance
                        LatLng(
                            currentPoint.latitude + (nextPoint.latitude - currentPoint.latitude) * fraction,
                            currentPoint.longitude + (nextPoint.longitude - currentPoint.longitude) * fraction
                        )
                    }

                    val location = Location(LocationManager.GPS_PROVIDER).apply {
                        latitude = currentPoint.latitude
                        longitude = currentPoint.longitude
                        time = System.currentTimeMillis()
                        elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
                        accuracy = 1f
                        speed = currentSpeedMps
                    }

                    locationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, location)
                    _serviceStateFlow.value =
                        LocationMockServiceState.MockingLocation(currentPoint)

                    delay(updateIntervalMs)
                    Log.d("MyLog","dElAy")
                }
            }.onFailure { exception ->
                if (exception is SecurityException) {
                    Log.d("MyLog","SecurityException")
                    _serviceStateFlow.value =
                        LocationMockServiceState.Failed("Please set this app as a mock provider in the developer options")
                } else {
                    Log.d("MyLog","Something went wrong $exception")
                    _serviceStateFlow.value =
                        LocationMockServiceState.Failed("Something went wrong")
                }
            }
        }

    }


    inner class LocalBinder : Binder() {
        fun getService(): UpdateLocationService = this@UpdateLocationService
        fun setSpeedKmh(speedKmh: Float) {
            currentSpeedMps = speedKmh / 3.6f
        }

        fun getSpeedKmh(): Float = currentSpeedMps * 3.6f
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        _serviceStateFlow.value = LocationMockServiceState.Idle
    }

    override fun onDestroy() {
        locationJob?.cancel()
        serviceScope.cancel()
        _serviceStateFlow.value = LocationMockServiceState.Idle
        super.onDestroy()
    }


    fun stopMocking() {
        locationJob?.cancel()
        _serviceStateFlow.value = LocationMockServiceState.Idle
    }


    private fun createNotification() {
        val notification = NotificationCompat.Builder(this, "running_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Location spoofing in progress").setContentText("")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT).build()
        startForeground(1, notification)
    }
}