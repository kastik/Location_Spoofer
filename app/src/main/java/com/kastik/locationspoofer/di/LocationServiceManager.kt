package com.kastik.locationspoofer.di

import android.content.*
import android.os.IBinder
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.maps.routing.v2.Polyline
import com.google.maps.routing.v2.Route
import com.kastik.locationspoofer.service.LocationMockServiceState
import com.kastik.locationspoofer.service.UpdateLocationService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationServiceManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var service: UpdateLocationService? = null
    private var isBound = false

    private val _serviceState = MutableStateFlow<LocationMockServiceState>(LocationMockServiceState.Idle)
    val serviceState = _serviceState.asStateFlow()

    private val serviceIntent by lazy {
        Intent(context, UpdateLocationService::class.java)
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            Log.d("MyLog", "onServiceConnected: $name")
            val localBinder = binder as UpdateLocationService.LocalBinder
            service = localBinder.getService()
            isBound = true
            CoroutineScope(Dispatchers.Main).launch {
                service!!.serviceStateFlow.collect { state ->
                    _serviceState.value = state
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Log.d("MyLog", "disconnect service: $name")

            service = null
            isBound = false
            _serviceState.value = LocationMockServiceState.Idle
        }
    }

    fun startAndBindService() {
        Log.d("MyLog", "start and bind ()")

        context.startService(serviceIntent)
        context.bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)
    }

    fun stopAndUnbindService() {
        Log.d("MyLog", "stop and unbind ()")

        if (isBound) {
            context.unbindService(connection)
            isBound = false
        }
        context.stopService(serviceIntent)
        service = null
        _serviceState.value = LocationMockServiceState.Idle
    }

    fun startMocking(route: Route) {
        Log.d("MyLog", "start mock")

        if(service == null){
            startAndBindService()
        }
        service?.startMockingLocation(route)
    }

    fun startMocking(polyline: Polyline) {
        Log.d("MyLog", "start mock")

        if(service == null){
            startAndBindService()
        }
        service?.startMockingLocation(polyline)
    }

    fun startMocking(latLng: LatLng) {
        Log.d("MyLog", "start mock")

        if(service == null){
            startAndBindService()
        }
        service?.startMockingLocation(latLng)
    }

    fun stopMocking() {
        Log.d("MyLog", "stop mock")

        service?.stopMocking()
    }

    fun setSpeedKmh(speedKmh: Float) {
        service?.let {
            (it.onBind(serviceIntent) as? UpdateLocationService.LocalBinder)?.setSpeedKmh(speedKmh)
        }
    }

    fun getSpeedKmh(): Float? {
        return (service?.onBind(serviceIntent) as? UpdateLocationService.LocalBinder)?.getSpeedKmh()
    }
}
