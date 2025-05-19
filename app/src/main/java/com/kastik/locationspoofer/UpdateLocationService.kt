package com.kastik.locationspoofer

import android.app.Service
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.location.provider.ProviderProperties
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class UpdateLocationService : Service() {

    private val _serviceState: MutableState<LocationMockServiceState> =
        mutableStateOf(LocationMockServiceState.Idle)
    val serviceState: State<LocationMockServiceState> = _serviceState

    private var locationJob: Job? = null
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val binder = LocalBinder()


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        _serviceState.value = LocationMockServiceState.Idle
        return START_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun setLocation(latLng: LatLng) {
        locationJob?.cancel()
        createNotification()
        locationJob = serviceScope.launch {
            val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
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

            locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true)
            val location = Location(LocationManager.GPS_PROVIDER)

            while (true) {
                location.latitude = latLng.latitude
                location.longitude = latLng.longitude
                location.time = System.currentTimeMillis()
                location.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
                location.setAccuracy(5F)
                locationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, location)
                _serviceState.value = LocationMockServiceState.MockingLocation(latLng)
                delay(2000)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun setLocation(latLng: List<LatLng>) {
        _serviceState.value = LocationMockServiceState.MockingLocation(latLng[0])
        locationJob?.cancel()
        createNotification()
        locationJob = serviceScope.launch {
            val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

            locationManager.addTestProvider(
                LocationManager.GPS_PROVIDER,
                false, false, false, false,
                true, true, true,
                ProviderProperties.POWER_USAGE_LOW,
                ProviderProperties.ACCURACY_FINE
            )
            locationManager.addTestProvider(
                LocationManager.FUSED_PROVIDER,
                false, false, false, false,
                true, true, true,
                ProviderProperties.POWER_USAGE_LOW,
                ProviderProperties.ACCURACY_FINE
            )

            locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true)
            locationManager.setTestProviderEnabled(LocationManager.FUSED_PROVIDER, true)

            val speedMps = 30.0f // Speed in meters per second
            val stepMeters = 1f  // Distance between points

            // Generate all intermediate points
            val fullPath = mutableListOf<LatLng>()
            for ((start, end) in latLng.zipWithNext()) {
                fullPath += generateIntermediateLatLngPoints(start, end, stepMeters).dropLast(1)
            }
            fullPath += latLng.last() // Ensure the final destination is included

            for ((point, nextPoint) in fullPath.zipWithNext()) {
                val location = Location(LocationManager.GPS_PROVIDER).apply {
                    latitude = point.latitude
                    longitude = point.longitude
                    time = System.currentTimeMillis()
                    elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
                    accuracy = 1F
                }
                locationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, location)
                _serviceState.value = LocationMockServiceState.MockingLocation(point)
                val delay = getDelayMillisBetweenPoints(point, nextPoint, speedMps, stepMeters)
                delay(delay)
            }
        }
    }

    fun generateIntermediateLatLngPoints(
        p1: LatLng,
        p2: LatLng,
        stepMeters: Float = 1f
    ): List<LatLng> {
        val results = FloatArray(1)
        Location.distanceBetween(p1.latitude, p1.longitude, p2.latitude, p2.longitude, results)
        val totalDistance = results[0]

        val steps = (totalDistance / stepMeters).toInt().coerceAtLeast(1)
        val points = mutableListOf<LatLng>()

        val lat1 = Math.toRadians(p1.latitude)
        val lon1 = Math.toRadians(p1.longitude)
        val lat2 = Math.toRadians(p2.latitude)
        val lon2 = Math.toRadians(p2.longitude)

        for (i in 0..steps) {
            val fraction = i.toDouble() / steps
            val A =
                sin((1 - fraction) * totalDistance / EARTH_RADIUS_METERS) / sin(totalDistance / EARTH_RADIUS_METERS)
            val B =
                sin(fraction * totalDistance / EARTH_RADIUS_METERS) / sin(totalDistance / EARTH_RADIUS_METERS)

            val x = A * cos(lat1) * cos(lon1) + B * cos(lat2) * cos(lon2)
            val y = A * cos(lat1) * sin(lon1) + B * cos(lat2) * sin(lon2)
            val z = A * sin(lat1) + B * sin(lat2)

            val newLat = atan2(z, sqrt(x * x + y * y))
            val newLon = atan2(y, x)

            points.add(LatLng(Math.toDegrees(newLat), Math.toDegrees(newLon)))
        }

        return points
    }

    private val EARTH_RADIUS_METERS = 6371000.0

    fun getDelayMillisBetweenPoints(
        p1: LatLng,
        p2: LatLng,
        speedMps: Float,
        stepMeters: Float = 1f
    ): Long {
        val results = FloatArray(1)
        Location.distanceBetween(p1.latitude, p1.longitude, p2.latitude, p2.longitude, results)
        val totalDistance = results[0] // in meters

        val steps = (totalDistance / stepMeters).coerceAtLeast(1f) // avoid division by zero
        val stepDistance = totalDistance / steps

        // time = distance / speed => for each step
        return ((stepDistance / speedMps) * 1000).toLong()
    }

    inner class LocalBinder : Binder() {
        fun getService(): UpdateLocationService = this@UpdateLocationService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        _serviceState.value = LocationMockServiceState.Idle
    }

    override fun onDestroy() {
        locationJob?.cancel()
        serviceScope.cancel()
        _serviceState.value = LocationMockServiceState.Idle
        super.onDestroy()
    }

    fun stopMocking() {
        locationJob?.cancel()
        serviceScope.cancel()
        _serviceState.value = LocationMockServiceState.Idle
    }

    private fun createNotification() {
        val notification =
            NotificationCompat.Builder(this, "running_channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Location spoofing in progress")
                .setContentText("")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()


        startForeground(1, notification)
    }
}


sealed class LocationMockServiceState() {
    object Idle : LocationMockServiceState()
    data class Failed(val error: String) : LocationMockServiceState() //TODO ON EXCEPTION
    data class MockingLocation(val latLng: LatLng) : LocationMockServiceState()
}