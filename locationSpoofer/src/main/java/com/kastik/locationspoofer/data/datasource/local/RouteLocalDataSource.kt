package com.kastik.locationspoofer.data.datasource.local

import androidx.datastore.core.DataStore
import com.kastik.locationspoofer.SavedRoute
import com.kastik.locationspoofer.SavedRoutes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class RouteLocalDataSource(
    private val routesDataStore: DataStore<SavedRoutes>
){
    val savedRoutesFlow: Flow<SavedRoutes> = routesDataStore.data

    suspend fun saveRoute(route: SavedRoute) {
        routesDataStore.updateData { currentData ->
            currentData.toBuilder()
                .addRoutes(route)
                .build()
        }
    }

    suspend fun deleteRoute(route: SavedRoute) {
        routesDataStore.updateData { currentData ->
            val updatedRoutes = currentData.routesList.filterNot {
                it.route.polyline != route.route.polyline
            }
            currentData.toBuilder()
                .clearRoutes()
                .addAllRoutes(updatedRoutes)
                .build()
        }
    }

    suspend fun updateRoute(route: SavedRoute) {
        routesDataStore.updateData { currentData ->
            val updatedRoutes = currentData.routesList.map { existing ->
                if (existing.route.polyline == route.route.polyline) {
                    route
                } else {
                    existing
                }
            }
            currentData.toBuilder()
                .clearRoutes()
                .addAllRoutes(updatedRoutes)
                .build()
        }
    }

    suspend fun isRouteSaved(route: SavedRoute): Boolean {
        val currentData = routesDataStore.data.first()
        return currentData.routesList.any { it.route.polyline == route.route.polyline }

    }
}