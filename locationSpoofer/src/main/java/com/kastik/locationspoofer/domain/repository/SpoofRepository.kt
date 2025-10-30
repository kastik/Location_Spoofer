package com.kastik.locationspoofer.domain.repository

import com.kastik.locationspoofer.domain.model.LatLngDomain
import com.kastik.locationspoofer.domain.model.RouteDomain
import com.kastik.locationspoofer.ui.screens.mapScreen.SpoofState
import kotlinx.coroutines.flow.StateFlow


interface SpoofRepository {
    val spoofState: StateFlow<SpoofState>
    fun spoofLocation(route: RouteDomain, loopOnFinish: Boolean,resetOnFinish: Boolean)
    fun spoofLocation(latLng: LatLngDomain)
    fun stopSpoofing()
}