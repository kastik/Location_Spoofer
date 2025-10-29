package com.kastik.locationspoofer.domain.repository

import com.kastik.locationspoofer.domain.model.LatLngDomain
import com.kastik.locationspoofer.domain.model.PlaceDomain
import kotlinx.coroutines.flow.Flow

interface PlacesRepository {
    val savedPlaces: Flow<List<PlaceDomain>>
    suspend fun savePlace(place: PlaceDomain)
    suspend fun deletePlace(place: PlaceDomain)
    suspend fun checkIfPlaceIsStored(place: PlaceDomain): Boolean
    suspend fun updatePlace(place: PlaceDomain)
    suspend fun searchPlaces(query: String): List<PlaceDomain>
    suspend fun getPlaceDetailsWithId(id: String): PlaceDomain?
}