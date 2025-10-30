package com.kastik.locationspoofer.data.datasource.local

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.kastik.locationspoofer.SpoofService
import com.kastik.locationspoofer.domain.model.LatLngDomain
import com.kastik.locationspoofer.domain.model.RouteDomain
import com.kastik.locationspoofer.ui.screens.mapScreen.SpoofState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


interface SpoofDataSource {
    val spoofState: StateFlow<SpoofState>
    fun startSpoofing(route: RouteDomain, loopOnFinish: Boolean, resetOnFinish: Boolean)
    fun startSpoofing(latLng: LatLngDomain)
    fun stopSpoofing()
}

class SpoofDataSourceImpl(
    private val context: Context
) : SpoofDataSource {

    private var myService: SpoofService.UpdateLocationBinder? = null
    private var isBound = false

    private val _spoofState = MutableStateFlow<SpoofState>(SpoofState.Idle)
    override val spoofState: StateFlow<SpoofState> get() = _spoofState

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as SpoofService.UpdateLocationBinder
            myService = binder
            isBound = true

            CoroutineScope(Dispatchers.Default).launch {
                binder.spoofState.collect { _spoofState.value = it }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
            myService = null
            _spoofState.value = SpoofState.Idle
        }
    }

    init {
        Intent(context, SpoofService::class.java).also {
            context.bindService(it, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun startSpoofing(route: RouteDomain, loopOnFinish: Boolean,resetOnFinish: Boolean) {
        myService?.startSpoofing(route, loopOnFinish,resetOnFinish)
    }

    override fun startSpoofing(latLng: LatLngDomain) {
        myService?.startSpoofing(latLng)
    }

    override fun stopSpoofing() {
        myService?.stopSpoofing()
    }

    fun cleanup() {
        if (isBound) {
            context.unbindService(connection)
            isBound = false
        }
    }
}