package com.kastik.locationspoofer.data.models

import com.google.android.gms.maps.model.LatLng

sealed class ServiceState {
    data object Stopped : ServiceState()
    data class Running(val spoofLocation: LatLng) : ServiceState()
}