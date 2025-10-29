package com.kastik.locationspoofer.data.datasource.remote

import com.google.maps.places.v1.AutocompletePlacesRequest
import com.google.maps.places.v1.GetPlaceRequest
import com.google.maps.places.v1.Place
import com.google.maps.places.v1.PlacesGrpc
import com.kastik.locationspoofer.data.mapers.toPlaceDomain
import com.kastik.locationspoofer.data.mapers.toPlacesDomain
import com.kastik.locationspoofer.domain.model.PlaceDomain
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlacesRemoteDataSource @Inject constructor(
    private val placesStub: PlacesGrpc.PlacesBlockingStub
) {
    suspend fun searchPlaces(query: String): List<PlaceDomain> = runCatching {
        val request = AutocompletePlacesRequest.newBuilder()
            .setInput(query)
            .build()

        val response = placesStub.autocompletePlaces(request)

        response.toPlacesDomain()
    }.getOrElse { e ->
        e.printStackTrace()
        emptyList()
    }

    suspend fun getPlaceDetailsWithId(id: String): PlaceDomain? = runCatching {
        val request = GetPlaceRequest.newBuilder()
            .setName("places/$id") //TODO
            .build()

        val response: Place = placesStub.getPlace(request)
        response.toPlaceDomain()
    }.getOrElse { e ->
        e.printStackTrace()
        null
    }
}