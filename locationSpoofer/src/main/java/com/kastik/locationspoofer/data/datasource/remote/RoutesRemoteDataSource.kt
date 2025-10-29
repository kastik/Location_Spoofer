package com.kastik.locationspoofer.data.datasource.remote

import com.google.maps.routing.v2.ComputeRoutesRequest
import com.google.maps.routing.v2.Route
import com.google.maps.routing.v2.RoutesGrpc
import com.kastik.locationspoofer.data.mapers.toRouteDomain
import com.kastik.locationspoofer.data.mapers.toWaypoint
import com.kastik.locationspoofer.domain.model.PlaceDomain
import com.kastik.locationspoofer.domain.model.RouteDomain
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoutesRemoteDataSource @Inject constructor(
    private val routesStub: RoutesGrpc.RoutesBlockingStub
) {
    suspend fun computeRoute(waypoints: List<PlaceDomain>): RouteDomain? = runCatching {
        if (waypoints.isEmpty()) return null

        val request = ComputeRoutesRequest.newBuilder()
            .setOrigin(waypoints.first().toWaypoint())
            .setDestination(waypoints.last().toWaypoint())
            .addAllIntermediates(waypoints.drop(1).dropLast(1).map { it.toWaypoint() })
            .build()

        val response = routesStub.computeRoutes(request)
        val route: Route = response.getRoutes(0)

        route.toRouteDomain()
    }.getOrElse { e ->
        e.printStackTrace()
        null
    }
}