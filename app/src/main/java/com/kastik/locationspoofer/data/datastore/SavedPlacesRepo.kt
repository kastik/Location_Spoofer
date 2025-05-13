package com.kastik.locationspoofer.data.datastore

import androidx.datastore.core.DataStore
import com.kastik.locationspoofer.debug.Place
import com.kastik.locationspoofer.debug.SavedPlaces
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class SavedPlacesRepo(private val savedPlacesRepo: DataStore<SavedPlaces>) {

    val savedPlacesFlow: Flow<SavedPlaces> = savedPlacesRepo.data

    suspend fun addNewPlace(place: Place) {
        savedPlacesRepo.updateData { currentData ->
            currentData.toBuilder()
                .addPlace(place) // This appends the new place to the repeated field
                .build()
        }
    }


    suspend fun deletePlaceById(placeId: String) {
        savedPlacesRepo.updateData { currentData ->
            val filteredPlaces = currentData.placeList
                .filter { it.placeId != placeId }

            currentData.toBuilder()
                .clearPlace()               // Clear all existing places
                .addAllPlace(filteredPlaces) // Add back only the ones to keep
                .build()
        }
    }

    suspend fun checkIfPlaceExists(placeId: String?): Boolean {
        if (placeId.isNullOrBlank()){
            return false
        }else {
            val currentData = savedPlacesRepo.data.first()
            return currentData.placeList.any { it.placeId == placeId }
        }
    }

}