package com.kastik.locationspoofer.ui.screens.mapScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kastik.locationspoofer.UserPreferences
import com.kastik.locationspoofer.data.datasource.local.UserPreferencesLocalDataSource
import com.kastik.locationspoofer.data.mapers.toGmsLatLng
import com.kastik.locationspoofer.data.mapers.toLatLngBounds
import com.kastik.locationspoofer.data.mapers.toViewport
import com.kastik.locationspoofer.domain.model.LatLngDomain
import com.kastik.locationspoofer.domain.model.PlaceDomain
import com.kastik.locationspoofer.domain.model.RouteDomain
import com.kastik.locationspoofer.domain.usecase.CheckIfPlaceIsSavedUseCase
import com.kastik.locationspoofer.domain.usecase.CheckIfRouteIsSavedUseCase
import com.kastik.locationspoofer.domain.usecase.ComputeRouteUseCase
import com.kastik.locationspoofer.domain.usecase.DeletePlaceUseCase
import com.kastik.locationspoofer.domain.usecase.DeleteRouteUseCase
import com.kastik.locationspoofer.domain.usecase.GetPlaceDetailsWithId
import com.kastik.locationspoofer.domain.usecase.GetSavedPlacesUseCase
import com.kastik.locationspoofer.domain.usecase.GetSpoofingStateUseCase
import com.kastik.locationspoofer.domain.usecase.SavePlaceUseCase
import com.kastik.locationspoofer.domain.usecase.SaveRouteUseCase
import com.kastik.locationspoofer.domain.usecase.SearchPlacesUseCase
import com.kastik.locationspoofer.domain.usecase.StartSpoofingUseCase
import com.kastik.locationspoofer.domain.usecase.StopSpoofingUseCase
import com.kastik.locationspoofer.ui.components.DialogState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MapScreenViewModel @Inject constructor(
    private val getSavedPlacesUseCase: GetSavedPlacesUseCase,
    private val searchPlacesUseCase: SearchPlacesUseCase,
    private val computeRouteUseCase: ComputeRouteUseCase,
    val preferencesRepo: UserPreferencesLocalDataSource,
    private val saveRouteUseCase: SaveRouteUseCase,
    private val savePlaceUseCase: SavePlaceUseCase,
    private val checkIfPlaceIsSavedUseCase: CheckIfPlaceIsSavedUseCase,
    private val checkIfRouteIsSavedUseCase: CheckIfRouteIsSavedUseCase,
    private val getPlaceDetailsWithIdUseCase: GetPlaceDetailsWithId,
    val deleteRouteUseCase: DeleteRouteUseCase,
    val deletePlaceUseCase: DeletePlaceUseCase,
    val getSpoofingStateUseCase: GetSpoofingStateUseCase,
    val stopSpoofingUseCase: StopSpoofingUseCase,
    val startSpoofingUseCase: StartSpoofingUseCase
) : ViewModel() {

    var uiState by mutableStateOf(MapScreenUiState())
        private set


    init {
        observeSavedPlaces()
        observeServiceState()
    }


    fun startSpoofing() {
        val route = uiState.activeRoute
        val place = uiState.activePlaces.firstOrNull()?.location
        when {
            route != null -> startSpoofingUseCase(
                routeDomain = route,
                loopOnFinish = /* from prefs */ false,
                resetOnFinish = false
            )

            place != null -> startSpoofingUseCase(
                latLngDomain = place,
            )
        }
    }

    fun stopSpoofing() {
        stopSpoofingUseCase()
    }

    private fun observeSavedPlaces() {
        viewModelScope.launch {
            getSavedPlacesUseCase().collect {
                uiState = uiState.copy(
                    savedPlaces = it
                )
            }
        }
    }

    private fun observeServiceState() {
        viewModelScope.launch {
            getSpoofingStateUseCase().collect { s ->
                uiState = uiState.copy(
                    fabState = uiState.fabState.copy(isSpoofing = s is SpoofState.Spoofing)
                )
            }
        }
    }

    fun confirmSaveDialog(
        newName: String
    ) {
        var place = uiState.activePlaces.firstOrNull() ?: return
        place = place.copy(
            customName = newName
        )
        viewModelScope.launch {
            runCatching {
                savePlaceUseCase(place)
            }.onSuccess {
                uiState = uiState.copy(
                    fabState = uiState.fabState.copy(
                        isActiveSaved = true
                    ),
                    dialogState = DialogState.None,
                    error = null
                )
            }.onFailure {
                uiState = uiState.copy(
                    error = AppError(
                        title = "Something went wrong",
                        action = {
                            uiState = uiState.copy(
                                dialogState = DialogState.None,
                                error = null
                            )
                        },
                        dismiss = {
                            uiState = uiState.copy(
                                dialogState = DialogState.None,
                                error = null
                            )
                        }
                    )
                )
            }
        }
    }

    fun confirmSaveDialog(
        newOriginName: String,
        newDestinationName: String
    ) {
        var route = uiState.activeRoute ?: return
        route = route.copy(
            origin = newOriginName,
            destination = newDestinationName
        )
        viewModelScope.launch {
            runCatching {
                saveRouteUseCase(route)
            }.onSuccess {
                uiState = uiState.copy(
                    fabState = uiState.fabState.copy(
                        isActiveSaved = true
                    ),
                    dialogState = DialogState.None,
                    error = null
                )
            }.onFailure {
                uiState = uiState.copy(
                    error = AppError(
                        title = "Something went wrong",
                        action = {
                            uiState = uiState.copy(
                                dialogState = DialogState.None,
                                error = null
                            )
                        },
                        dismiss = {
                            uiState = uiState.copy(
                                dialogState = DialogState.None,
                                error = null
                            )
                        }
                    )
                )
            }
        }
    }

    fun clearCameraTarget() {
        uiState = uiState.copy(animateCameraTarget = CameraTarget.None)
    }

    fun animateTo(target: CameraTarget) {
        uiState = uiState.copy(animateCameraTarget = target)
    }


    val userPreferences: StateFlow<UserPreferences> = preferencesRepo.userPreferencesFlow.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserPreferences.getDefaultInstance()
    )

    fun searchForPlace(query: String) {
        viewModelScope.launch {
            runCatching {
                val results = searchPlacesUseCase(query)
                uiState = uiState.copy(
                    searchResults = results
                )
            }.onFailure {
                uiState = uiState.copy(
                    searchResults = emptyList(),
                    error =
                        AppError(
                            title = "Something went wrong while searching places",
                            action = {
                                uiState = uiState.copy(
                                    error = null
                                )
                            },
                            dismiss = {
                                uiState = uiState.copy(
                                    error = null
                                )
                            }
                        )

                )
            }
        }
    }


    fun moveCameraToResult(place: PlaceDomain) {
        viewModelScope.launch(Dispatchers.IO) {
            place.id?.let { id ->
                val placeDomainWithBounds = getPlaceDetailsWithIdUseCase(id)

                val target = placeDomainWithBounds?.viewport?.let {
                    CameraTarget.Bounds(it.toLatLngBounds())
                } ?: CameraTarget.Point(place.location.toGmsLatLng())

                uiState = uiState.copy(animateCameraTarget = target)
            }
        }
    }


    fun openSaveDialog() {
        val route = uiState.activeRoute
        val places = uiState.activePlaces
        if (places.size >= 2 && route != null) {
            uiState = uiState.copy(
                dialogState = DialogState.SaveRoute(
                    route = route
                ),
            )
        } else if (uiState.activePlaces.isNotEmpty()) {
            uiState = uiState.copy(
                dialogState = DialogState.SavePlace(
                    places.first()
                ),
            )
        }
    }

    fun openDeleteDialog() {
        val route = uiState.activeRoute
        val places = uiState.activePlaces
        if (places.size >= 2 && route != null) {
            uiState = uiState.copy(
                dialogState = DialogState.DeleteRoute(
                    route = route
                ),
            )
        } else if (uiState.activePlaces.isNotEmpty()) {
            uiState = uiState.copy(
                dialogState = DialogState.DeletePlace(
                    places.first()
                ),
            )
        }
    }

    fun dismissDialogs() {
        uiState = uiState.copy(
            dialogState = DialogState.None,
        )
    }

    fun deleteRoute(route: RouteDomain) {
        viewModelScope.launch {
            runCatching {
                deleteRouteUseCase(route)
            }.onSuccess {
                uiState = uiState.copy(
                    fabState = uiState.fabState.copy(
                        isActiveSaved = false
                    ),
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
                uiState = uiState.copy(
                    fabState = uiState.fabState.copy(
                        isActiveSaved = false
                    ),
                    dialogState = DialogState.None
                )
            }
        }
    }

    fun addMarker(
        place: PlaceDomain,
    ) {
        viewModelScope.launch {
            val newMarkers = uiState.activePlaces + place

            if (newMarkers.size >= 2) {
                val origin = newMarkers.first()
                val destination = newMarkers.last()
                val computedRoute = computeRouteUseCase(newMarkers)

                val updatedRoute = computedRoute.copy(
                    origin = origin.customName ?: origin.name,
                    destination = destination.name
                )
                val isRouteSaved = checkIfRouteIsSavedUseCase(updatedRoute)


                uiState = uiState.copy(
                    activePlaces = newMarkers,
                    activeRoute = updatedRoute,
                    fabState = uiState.fabState.copy(
                        showSaveButton = true,
                        isActiveSaved = isRouteSaved,
                        showSpoofButton = true,
                    ),
                )
                return@launch
            }

            if (newMarkers.isNotEmpty()) {
                val isPlaceSaved = checkIfPlaceIsSavedUseCase(newMarkers.first())
                uiState = uiState.copy(
                    activePlaces = newMarkers,
                    fabState = uiState.fabState.copy(
                        showSaveButton = true,
                        isActiveSaved = isPlaceSaved,
                        showSpoofButton = true,
                    )
                )
                return@launch
            }
        }

    }

    fun removeMarker(marker: LatLngDomain) {
        uiState = uiState.copy(
            activePlaces = uiState.activePlaces.filter { it.location != marker },

            )
    }


    fun clearMarkersAndRoute() {
        uiState = uiState.copy(
            activePlaces = emptyList(),
            activeRoute = null,
            fabState = uiState.fabState.copy(
                showSaveButton = false,
                isActiveSaved = false
            ),
        )
    }
}