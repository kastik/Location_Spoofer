package com.kastik.locationspoofer.service

import com.google.android.gms.maps.model.LatLng

sealed class LocationMockServiceState() {
    data object Idle : LocationMockServiceState()
    data class Failed(val error: String) : LocationMockServiceState()
    data class MockingLocation(val latLng: LatLng) : LocationMockServiceState()
}