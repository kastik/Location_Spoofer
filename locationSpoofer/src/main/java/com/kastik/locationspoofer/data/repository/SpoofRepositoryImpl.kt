package com.kastik.locationspoofer.data.repository

import com.kastik.locationspoofer.data.datasource.local.SpoofDataSource
import com.kastik.locationspoofer.domain.model.LatLngDomain
import com.kastik.locationspoofer.domain.model.RouteDomain
import com.kastik.locationspoofer.domain.repository.SpoofRepository
import com.kastik.locationspoofer.ui.screens.mapScreen.SpoofState
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class SpoofRepositoryImpl @Inject constructor(
    private val spoofDataSource: SpoofDataSource,
) : SpoofRepository {

    override val spoofState: StateFlow<SpoofState> = spoofDataSource.spoofState

    override fun spoofLocation(
        route: RouteDomain,
        loopOnFinish: Boolean,
        resetOnFinish: Boolean
    ) {
        spoofDataSource.startSpoofing(route, loopOnFinish,resetOnFinish)
    }

    override fun spoofLocation(latLng: LatLngDomain) {
        spoofDataSource.startSpoofing(latLng)
    }

    override fun stopSpoofing() {
        spoofDataSource.stopSpoofing()
    }

}
