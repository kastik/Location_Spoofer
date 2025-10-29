package com.kastik.locationspoofer.ui.screens.savedRoutesScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kastik.locationspoofer.domain.model.PlaceDomain
import com.kastik.locationspoofer.domain.model.RouteDomain
import com.kastik.locationspoofer.domain.usecase.DeletePlaceUseCase
import com.kastik.locationspoofer.domain.usecase.DeleteRouteUseCase
import com.kastik.locationspoofer.domain.usecase.GetSavedPlacesUseCase
import com.kastik.locationspoofer.domain.usecase.GetSavedRoutesUseCase
import com.kastik.locationspoofer.domain.usecase.UpdatePlaceUseCase
import com.kastik.locationspoofer.domain.usecase.UpdateRouteUseCase
import com.kastik.locationspoofer.ui.components.DialogState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@HiltViewModel
class SavedRoutesScreenViewModel @Inject constructor(
    val getSavedPlacesUseCase: GetSavedPlacesUseCase,
    val getSavedRoutesUseCase: GetSavedRoutesUseCase,
    val deleteRouteUseCase: DeleteRouteUseCase,
    val deletePlaceUseCase: DeletePlaceUseCase,
    val updateRouteUseCase: UpdateRouteUseCase,
    val updatePlaceUseCase: UpdatePlaceUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SavedRoutesScreenState())
    val uiState: StateFlow<SavedRoutesScreenState> = _uiState

    init {
        viewModelScope.launch {
            combine(
                getSavedPlacesUseCase(), getSavedRoutesUseCase()
            ) { places, routes ->
                _uiState.value.copy(
                    savedPlaces = places, savedRoutes = routes
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun deleteRoute(route: RouteDomain) {
        viewModelScope.launch {
            runCatching {
                deleteRouteUseCase(route)
            }.onSuccess {
                _uiState.value = _uiState.value.copy(
                    dialogState = DialogState.None
                )
            }
        }
    }

    fun deletePlace(place: PlaceDomain) {
        viewModelScope.launch {
            runCatching {
                deletePlaceUseCase(place)
            }.onSuccess {
                _uiState.value = _uiState.value.copy(
                    dialogState = DialogState.None
                )
            }
        }
    }

    fun updateRoute(
        newName: String,
        newOriginName: String,
        newDestinationName: String,
        newSpeed: Double,
        newLoop: Boolean
    ) {
        viewModelScope.launch {
            val dialog = _uiState.value.dialogState as? DialogState.EditRoute ?: return@launch
            val newRoute = dialog.route.copy(
                nickName = newName,
                origin = newOriginName,
                destination = newDestinationName,
                speed = newSpeed,
                loop = newLoop
            )
            runCatching {
                updateRouteUseCase(newRoute)
            }.onSuccess {
                _uiState.value = _uiState.value.copy(
                    dialogState = DialogState.None
                )
            }
        }
    }

    fun updatePlace(
        newName: String
    ) {
        viewModelScope.launch {
            val dialog = _uiState.value.dialogState as? DialogState.EditPlace ?: return@launch
            val newPlace = dialog.place.copy(
                name = newName
            )
            runCatching {
                updatePlaceUseCase(newPlace)
            }.onSuccess {
                _uiState.value = _uiState.value.copy(
                    dialogState = DialogState.None
                )
            }
        }
    }

    fun dismissDialogs() {
        _uiState.value = _uiState.value.copy(
            dialogState = DialogState.None
        )
    }


    fun showEditDialog(route: RouteDomain) {
        _uiState.value = _uiState.value.copy(
            dialogState = DialogState.EditRoute(route)
        )
    }

    fun showEditDialog(place: PlaceDomain) {
        _uiState.value = _uiState.value.copy(
            dialogState = DialogState.EditPlace(place)
        )
    }

    fun showDeleteDialog(route: RouteDomain) {
        _uiState.value = _uiState.value.copy(
            dialogState = DialogState.DeleteRoute(route)
        )
    }

    fun showDeleteDialog(place: PlaceDomain) {
        _uiState.value = _uiState.value.copy(
            dialogState = DialogState.DeletePlace(place)
        )
    }
}