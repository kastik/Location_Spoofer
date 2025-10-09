package com.kastik.locationspoofer.ui.screens.savedRoutesScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kastik.locationspoofer.SavedRoute
import com.kastik.locationspoofer.SavedRoutes
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
    val savedRoutesRepository: SavedRouteRepo
) : ViewModel() {
    private val _savedRoutes = MutableStateFlow(SavedRoutes.getDefaultInstance())
    val savedRoutes: StateFlow<SavedRoutes> = _savedRoutes.asStateFlow()

    init {
        collectSavedRoutes()
    }

    private fun collectSavedRoutes() {
        viewModelScope.launch {
            savedRoutesRepository.savedRoutesFlow.collect { savedRoutes ->
                _savedRoutes.update {
                    Log.d(
                        "SavedRoutesScreenViewModel",
                        "Received saved routes: ${savedRoutes.routesList}"
                    )
                    savedRoutes
                }
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
            Log.d("MyLog", "Updating route: $updated")
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
