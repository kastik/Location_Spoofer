package com.kastik.locationspoofer.ui.screens.savedRoutesScreen

import com.kastik.locationspoofer.domain.model.PlaceDomain
import com.kastik.locationspoofer.domain.model.RouteDomain
import com.kastik.locationspoofer.ui.components.DialogState

data class SavedRoutesScreenState(
    val dialogState: DialogState = DialogState.None,
    val savedPlaces: List<PlaceDomain> = emptyList(),
    val savedRoutes: List<RouteDomain> = emptyList()
)