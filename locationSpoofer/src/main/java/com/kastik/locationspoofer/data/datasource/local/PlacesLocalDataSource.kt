package com.kastik.locationspoofer.data.datasource.local

import androidx.datastore.core.DataStore
import com.kastik.locationspoofer.SavedPlace
import com.kastik.locationspoofer.SavedPlaces
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class PlacesLocalDataSource(
    private val placesDataStore: DataStore<SavedPlaces>
) {
    val savedPlacesFlow: Flow<SavedPlaces> = placesDataStore.data

    suspend fun savePlace(place: SavedPlace) {
        placesDataStore.updateData { currentData ->
            currentData.toBuilder()
                .addPlace(place)
                .build()
        }
    }

    suspend fun deletePlace(place: SavedPlace) {
        placesDataStore.updateData { currentData ->
            val filteredPlaces = currentData.placeList
                .filter { it.place.location != place.place.location }
            currentData.toBuilder()
                .clearPlace()
                .addAllPlace(filteredPlaces)
                .build()
        }
    }

    suspend fun isPlaceSaved(place: SavedPlace): Boolean {
        val currentData = placesDataStore.data.first()
        return currentData.placeList.any { it.place.location == place.place.location }

    }

    suspend fun updatePlace(place: SavedPlace) {
        placesDataStore.updateData { currentData ->
            val updatedPlaces = currentData.placeList.map { existing ->
                if (existing.place.id == place.place.id) {
                    place
                } else {
                    existing
                }
            }
            currentData.toBuilder()
                .clearPlace()
                .addAllPlace(updatedPlaces)
                .build()
        }
    }
}