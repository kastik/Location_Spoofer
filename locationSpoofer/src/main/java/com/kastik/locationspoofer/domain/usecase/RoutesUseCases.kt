package com.kastik.locationspoofer.domain.usecase

import com.kastik.locationspoofer.domain.model.PlaceDomain
import com.kastik.locationspoofer.domain.model.RouteDomain
import com.kastik.locationspoofer.domain.repository.RoutesRepository


class GetSavedRoutesUseCase(
    private val repo: RoutesRepository
) {
    operator fun invoke() = repo.savedRoutes
}

class DeleteRouteUseCase(
    private val repo: RoutesRepository
) {
    suspend operator fun invoke(route: RouteDomain) = repo.deleteRoute(route)
}

class ComputeRouteUseCase(
    private val repo: RoutesRepository
) {
    suspend operator fun invoke(waypoints: List<PlaceDomain>): RouteDomain =
        repo.computeRoute(waypoints)
}

class SaveRouteUseCase(
    private val repo: RoutesRepository
) {
    suspend operator fun invoke(route: RouteDomain) = repo.saveRoute(route)
}

class UpdateRouteUseCase(
    private val repo: RoutesRepository
) {
    suspend operator fun invoke(route: RouteDomain) = repo.updateRoute(route)
}

class CheckIfRouteIsSavedUseCase(
    private val repo: RoutesRepository
) {
    suspend operator fun invoke(route: RouteDomain): Boolean =
        repo.checkIfRouteIsSaved(route)
}
