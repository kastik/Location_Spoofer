package com.kastik.locationspoofer.domain.usecase

import com.kastik.locationspoofer.domain.model.PlaceDomain
import com.kastik.locationspoofer.domain.repository.PlacesRepository

class GetSavedPlacesUseCase(
    private val repo: PlacesRepository
) {
    operator fun invoke() = repo.savedPlaces
}

class SavePlaceUseCase(
    private val repo: PlacesRepository
) {
    suspend operator fun invoke(place: PlaceDomain) = repo.savePlace(place)
}

class DeletePlaceUseCase(
    private val repo: PlacesRepository
) {
    suspend operator fun invoke(place: PlaceDomain) = repo.deletePlace(place)
}

class SearchPlacesUseCase(
    private val repo: PlacesRepository
) {
    suspend operator fun invoke(query: String): List<PlaceDomain> =
        repo.searchPlaces(query)
}

class  UpdatePlaceUseCase(
    private val repo: PlacesRepository
) {
    suspend operator fun invoke(place: PlaceDomain) =
        repo.updatePlace(place)
}

class CheckIfPlaceIsSavedUseCase(
    private val repo: PlacesRepository
) {
    suspend operator fun invoke(place: PlaceDomain): Boolean =
        repo.checkIfPlaceIsStored(place)
}

class GetPlaceDetailsWithId(
    private val repo: PlacesRepository
) {
    suspend operator fun invoke(id: String): PlaceDomain? =
        repo.getPlaceDetailsWithId(id)
}
