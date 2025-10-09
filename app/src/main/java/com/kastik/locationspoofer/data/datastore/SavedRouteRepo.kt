package com.kastik.locationspoofer.data.datastore

import androidx.datastore.core.DataStore
import com.google.maps.routing.v2.Route
import com.kastik.locationspoofer.SavedRoute
import com.kastik.locationspoofer.SavedRoutes
import kotlinx.coroutines.flow.Flow

class SavedRouteRepo(
    private val savedRoutesRepo: DataStore<SavedRoutes>
){
    val savedRoutesFlow: Flow<SavedRoutes> = savedRoutesRepo.data

    suspend fun addNewRoute(route: SavedRoute) {
        savedRoutesRepo.updateData { currentData ->
            currentData.toBuilder()
                .addRoutes(route)
                .build()
        }
    }

    suspend fun deleteRoute(route: SavedRoute) {
        savedRoutesRepo.updateData { currentData ->
            val updatedRoutes = currentData.routesList
                .filterNot { it == route }
            currentData.toBuilder()
                .clearRoutes()
                .addAllRoutes(updatedRoutes)
                .build()
        }
    }

    suspend fun updateRoute(updated: SavedRoute) {
        savedRoutesRepo.updateData { currentData ->
            val updatedRoutes = currentData.routesList.map { existing ->
                if (existing == updated) {
                    updated
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
}