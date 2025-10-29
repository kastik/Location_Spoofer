package com.kastik.locationspoofer.domain.repository

import com.kastik.locationspoofer.domain.model.PlaceDomain
import com.kastik.locationspoofer.domain.model.RouteDomain
import kotlinx.coroutines.flow.Flow

interface RoutesRepository {
    val savedRoutes: Flow<List<RouteDomain>>

    suspend fun saveRoute(route: RouteDomain)
    suspend fun deleteRoute(route: RouteDomain)
    suspend fun checkIfRouteIsSaved(route: RouteDomain): Boolean
    suspend fun updateRoute(route: RouteDomain)
    suspend fun computeRoute(waypoints: List<PlaceDomain>): RouteDomain
}