package com.kastik.locationspoofer.data.datastore

import androidx.datastore.core.DataStore
import com.google.maps.places.v1.Place
import com.kastik.locationspoofer.SavedPlaces
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class SavedPlacesRepo(
    private val savedPlacesRepo: DataStore<SavedPlaces>
){
    val savedPlacesFlow: Flow<SavedPlaces> = savedPlacesRepo.data

    suspend fun addNewPlace(place: Place) {
        savedPlacesRepo.updateData { currentData ->
            currentData.toBuilder()
                .addPlace(place)
                .build()
        }
    }


    suspend fun deletePlaceById(placeId: String) {
        savedPlacesRepo.updateData { currentData ->
            val filteredPlaces = currentData.placeList
                .filter { it.id != placeId }
            currentData.toBuilder()
                .clearPlace()
                .addAllPlace(filteredPlaces)
                .build()
        }
    }

    suspend fun checkIfPlaceExists(placeId: String?): Boolean {
        if (placeId.isNullOrBlank()){
            return false
        }else {
            val currentData = savedPlacesRepo.data.first()
            return currentData.placeList.any { it.id == placeId }
        }
    }
}