package com.kastik.locationspoofer.data.repository

import com.kastik.locationspoofer.data.datasource.local.PlacesLocalDataSource
import com.kastik.locationspoofer.data.datasource.remote.PlacesRemoteDataSource
import com.kastik.locationspoofer.data.mapers.toPlaceDomain
import com.kastik.locationspoofer.data.mapers.toSavedPlaceProto
import com.kastik.locationspoofer.domain.model.PlaceDomain
import com.kastik.locationspoofer.domain.repository.PlacesRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.map

class PlacesRepositoryImpl @Inject constructor(
    private val local: PlacesLocalDataSource,
    private val remote: PlacesRemoteDataSource
) : PlacesRepository {

    override val savedPlaces = local.savedPlacesFlow.map {
        it.placeList.mapNotNull { place -> place.toPlaceDomain() }
    }

    override suspend fun savePlace(place: PlaceDomain) =
        local.savePlace(place.toSavedPlaceProto())

    override suspend fun deletePlace(place: PlaceDomain) {
        local.deletePlace(place.toSavedPlaceProto())
    }

    override suspend fun checkIfPlaceIsStored(place: PlaceDomain): Boolean {
        return local.isPlaceSaved(place.toSavedPlaceProto())

    }

    override suspend fun updatePlace(place: PlaceDomain) {
        local.updatePlace(place.toSavedPlaceProto())
    }

    override suspend fun searchPlaces(query: String) =
        remote.searchPlaces(query)

    override suspend fun getPlaceDetailsWithId(id: String) =
        remote.getPlaceDetailsWithId(id)
}