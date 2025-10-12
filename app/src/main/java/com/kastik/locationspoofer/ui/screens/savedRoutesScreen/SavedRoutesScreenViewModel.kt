package com.kastik.locationspoofer.ui.screens.savedRoutesScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kastik.locationspoofer.SavedPlaces
import com.kastik.locationspoofer.SavedRoute
import com.kastik.locationspoofer.SavedRoutes
import com.kastik.locationspoofer.data.datastore.SavedPlacesRepo
import com.kastik.locationspoofer.data.datastore.SavedRouteRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SavedRoutesScreenViewModel @Inject constructor(
    val savedPlacesRepository: SavedPlacesRepo,
    val savedRoutesRepository: SavedRouteRepo
) : ViewModel() {
    private val _savedRoutes = MutableStateFlow(SavedRoutes.getDefaultInstance())
    val savedRoutes: StateFlow<SavedRoutes> = _savedRoutes.asStateFlow()
    private val _savedPlaces = MutableStateFlow(SavedPlaces.getDefaultInstance())
    val savedPlaces: StateFlow<SavedPlaces> = _savedPlaces.asStateFlow()

    init {
        collectSavedRoutesAndPlaces()
    }

    private fun collectSavedRoutesAndPlaces() {
        viewModelScope.launch {
            savedRoutesRepository.savedRoutesFlow.collect { savedRoutes ->
                _savedRoutes.update { savedRoutes }
            }
        }
        viewModelScope.launch {
            savedPlacesRepository.savedPlacesFlow.collect { savedPlaces ->
                _savedPlaces.update { savedPlaces }
            }
        }
    }


    fun deleteRoute(route: SavedRoute) {
        viewModelScope.launch {
            savedRoutesRepository.deleteRoute(route)
        }
    }

    fun updateRoute(
        route: SavedRoute,
        newOrigin: String,
        newDestination: String,
        newName: String,
        newLoop: Boolean,
        newSpeed: Float
    ) {
        viewModelScope.launch {
            val updated = route.toBuilder()
                .setOriginName(newOrigin)
                .setDestinationName(newDestination)
                .setNickname(newName)
                .setLoop(newLoop)
                .setSpeed(newSpeed)
                .build()
            savedRoutesRepository.updateRoute(updated)
        }
    }

//    fun onEditRoute(route: Route) {
//        _uiState.update { it.copy(editingRoute = route) }
//    }
//
//    fun onDismissDialog() {
//        _uiState.update { it.copy(editingRoute = null) }
//    }

    //  fun onSaveEditedRoute(updated: Route) {
    // Save logic here
    //     _uiState.update {
    //         it.copy(
    //             savedRoutes = updateRouteInList(it.savedRoutes, updated),
    //            editingRoute = null
    //        )
    //    }
    //}

    // private fun updateRouteInList(saved: SavedRoutes, updated: Route): SavedRoutes {
//       todo  return saved.toBuilder().clearRoute()
//            .addAllRoute(
//                saved.routeList.map {
//                    if (it.id == updated.id) updated else it
//                }
//            ).build()
    // }
}
