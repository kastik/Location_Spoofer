package com.kastik.locationspoofer.data.repository

import android.util.Log
import com.google.maps.routing.v2.Polyline
import com.google.maps.routing.v2.Route
import com.kastik.locationspoofer.SavedRoute
import com.kastik.locationspoofer.data.datasource.local.RouteLocalDataSource
import com.kastik.locationspoofer.data.datasource.remote.RoutesRemoteDataSource
import com.kastik.locationspoofer.data.mapers.toGmsLatLng
import com.kastik.locationspoofer.data.mapers.toLatLngDomain
import com.kastik.locationspoofer.data.mapers.toRouteDomain
import com.kastik.locationspoofer.data.mapers.toRouteProto
import com.kastik.locationspoofer.domain.model.PlaceDomain
import com.kastik.locationspoofer.domain.model.RouteDomain
import com.kastik.locationspoofer.domain.repository.RoutesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoutesRepositoryImpl @Inject constructor(
    private val local: RouteLocalDataSource,
    private val remote: RoutesRemoteDataSource
) : RoutesRepository {

    override val savedRoutes: Flow<List<RouteDomain>> = local.savedRoutesFlow.map { protoRoutes ->
        protoRoutes.routesList.map { saved ->
            saved.toRouteDomain()
        }
    }

    override suspend fun saveRoute(route: RouteDomain) {
        val proto = SavedRoute.newBuilder()
            .setOriginName(route.origin.orEmpty())
            .setDestinationName(route.destination.orEmpty())
            .setRoute(
                Route.newBuilder().setPolyline(
                    Polyline.newBuilder()
                        .setEncodedPolyline(route.encodedPolyline).build()
                ).build()
            ).build()
        local.saveRoute(proto)
    }

    override suspend fun computeRoute(waypoints: List<PlaceDomain>): RouteDomain {
        return remote.computeRoute(waypoints) ?: fallbackRoute(waypoints)
    }

    //TODO
    private fun fallbackRoute(waypoints: List<PlaceDomain>): RouteDomain {
        return RouteDomain(
            encodedPolyline = "",
            origin = "",
            destination = "",
            waypoints = listOf(waypoints.first().location.toGmsLatLng().toLatLngDomain()),
            nickName = "",
            loop = false,
            speed = 0.5
        )
    }


    override suspend fun deleteRoute(route: RouteDomain) {
        local.deleteRoute(
            route.toRouteProto()
        )
    }

    override suspend fun checkIfRouteIsSaved(route: RouteDomain): Boolean {
        return local.isRouteSaved(route.toRouteProto())
    }

    override suspend fun updateRoute(route: RouteDomain) {
        local.updateRoute(route.toRouteProto())
    }
}