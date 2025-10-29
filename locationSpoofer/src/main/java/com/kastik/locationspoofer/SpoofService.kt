package com.kastik.locationspoofer

import android.app.NotificationManager
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
import androidx.core.app.NotificationCompat
import com.kastik.locationspoofer.data.datasource.local.SpoofDataSource
import com.kastik.locationspoofer.domain.model.LatLngDomain
import com.kastik.locationspoofer.domain.model.RouteDomain
import com.kastik.locationspoofer.domain.usecase.EmulateRouteUseCase
import com.kastik.locationspoofer.ui.screens.mapScreen.SpoofState
import dagger.hilt.android.AndroidEntryPoint
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
import javax.inject.Inject

private const val EARTH_RADIUS_METERS = 6371000.0
@AndroidEntryPoint
class SpoofService : Service() {
    @Inject lateinit var emulateRouteUseCase: EmulateRouteUseCase

    private val _serviceStateFlow = MutableStateFlow<SpoofState>(SpoofState.Idle)

    private var locationJob: Job? = null
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val binder = UpdateLocationBinder()
    private var currentSpeedMps: Float = 16.666666f
    private val updateIntervalMs = 1000L

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        _serviceStateFlow.value = SpoofState.Idle
        return START_STICKY
    }


    private fun startSpoofing(route: RouteDomain) {
        locationJob?.cancel()
        locationJob = serviceScope.launch {
            runCatching {
                val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
                locationManager.removeTestProvider(LocationManager.GPS_PROVIDER)
                createTestProvider(locationManager)
                createNotification()
                locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true)

                emulateRouteUseCase(route).collect { point ->
                    val location = Location(LocationManager.GPS_PROVIDER).apply {
                        latitude = point.lat
                        longitude = point.lng
                        time = System.currentTimeMillis()
                        elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
                        accuracy = 1f
                        speed = route.speed.toFloat() // m/s
                    }
                    locationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, location)
                    _serviceStateFlow.value = SpoofState.Spoofing(point)
                }
            }.onFailure { handleError(it) }
        }
    }
    private fun startSpoofing(
        spoofLocation: LatLngDomain
    ) {
        locationJob?.cancel()
        locationJob = serviceScope.launch {
            runCatching {
                val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
                locationManager.removeTestProvider(LocationManager.GPS_PROVIDER)
                createTestProvider(locationManager)
                createNotification()
                locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true)
                val location = Location(LocationManager.GPS_PROVIDER).apply {
                    latitude = spoofLocation.lat
                    longitude = spoofLocation.lng
                    time = System.currentTimeMillis()
                    elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
                    accuracy = 1f
                    speed = currentSpeedMps
                }
                while (isActive) {
                    locationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, location)
                    _serviceStateFlow.value = SpoofState.Spoofing(spoofLocation)
                    delay(updateIntervalMs)
                }
            }.onFailure { exception ->
                handleError(exception)

            }.also {
                dismissNotification()
            }
        }
    }


    inner class UpdateLocationBinder : Binder(), SpoofDataSource {
        override val spoofState: StateFlow<SpoofState>
            get() = _serviceStateFlow

        override fun startSpoofing(route: RouteDomain, loop: Boolean) =
            startSpoofing(route)

        override fun startSpoofing(latLng: LatLngDomain) = this@SpoofService.startSpoofing(latLng)

        override fun stopSpoofing() {
            this@SpoofService.stopSpoofing()
        }
    }


    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onDestroy() {
        locationJob?.cancel()
        serviceScope.cancel()
        super.onDestroy()
    }


    private fun stopSpoofing() {
        locationJob?.cancel()
        _serviceStateFlow.value = SpoofState.Idle
    }


    private fun createNotification() {
        val notification = NotificationCompat.Builder(this, "running_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Location spoofing in progress")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT).build()
        startForeground(1, notification)
    }

    private fun dismissNotification() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(1)
    }

    @Suppress("DEPRECATION")
    private fun createTestProvider(locationManager: LocationManager) {
        val provider = LocationManager.GPS_PROVIDER

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            locationManager.addTestProvider(
                provider,
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
                provider,
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
    }

    private fun handleError(exception: Throwable) {
        if (exception is SecurityException) {
            Log.d("MyLog", "SecurityException")
            _serviceStateFlow.value =
                SpoofState.Failed("Please set this app as a mock provider in the developer options")
        } else {
            Log.d("MyLog", "Something went wrong $exception")
            _serviceStateFlow.value = SpoofState.Failed("Something went wrong")
        }
    }
}