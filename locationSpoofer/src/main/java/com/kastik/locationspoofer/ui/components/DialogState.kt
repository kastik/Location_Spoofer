package com.kastik.locationspoofer.ui.components

import com.kastik.locationspoofer.domain.model.PlaceDomain
import com.kastik.locationspoofer.domain.model.RouteDomain

sealed class DialogState {
    object None : DialogState()
    data class SavePlace(val place: PlaceDomain) : DialogState()
    data class SaveRoute(val route: RouteDomain) : DialogState()

    data class DeletePlace(val place: PlaceDomain) : DialogState()
    data class DeleteRoute(val route: RouteDomain) : DialogState()

    data class EditPlace(val place: PlaceDomain) : DialogState()
    data class EditRoute(val route: RouteDomain) : DialogState()

}