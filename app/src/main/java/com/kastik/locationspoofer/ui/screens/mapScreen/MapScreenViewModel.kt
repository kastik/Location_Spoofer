package com.kastik.locationspoofer.ui.screens.mapScreen

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.places.v1.AutocompletePlacesRequest
import com.google.maps.places.v1.GetPlaceRequest
import com.google.maps.places.v1.Place
import com.google.maps.places.v1.PlacesGrpc
import com.google.maps.routing.v2.ComputeRoutesRequest
import com.google.maps.routing.v2.Polyline
import com.google.maps.routing.v2.Route
import com.google.maps.routing.v2.RoutesGrpc
import com.kastik.locationspoofer.SavedPlaces
import com.kastik.locationspoofer.SavedRoute
import com.kastik.locationspoofer.SavedRoutes
import com.kastik.locationspoofer.UserPreferences
import com.kastik.locationspoofer.data.datastore.SavedPlacesRepo
import com.kastik.locationspoofer.data.datastore.SavedRouteRepo
import com.kastik.locationspoofer.data.datastore.UserPreferencesRepo
import com.kastik.locationspoofer.data.models.AppError
import com.kastik.locationspoofer.data.models.MarkerData
import com.kastik.locationspoofer.data.models.toGmsLatLng
import com.kastik.locationspoofer.data.models.toMarkerData
import com.kastik.locationspoofer.data.models.toPlaces
import com.kastik.locationspoofer.data.models.toWaypoint
import com.kastik.locationspoofer.di.LocationServiceManager
import com.kastik.locationspoofer.service.LocationMockServiceState
import com.kastik.locationspoofer.toLatLngBounds
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapScreenViewModel @Inject constructor(
    val savedPlacesRepo: SavedPlacesRepo,
    val preferencesRepo: UserPreferencesRepo,
    val savedRouteRepo: SavedRouteRepo,
    val routeStub: RoutesGrpc.RoutesBlockingStub,
    val placesStub: PlacesGrpc.PlacesBlockingStub,
    val serviceManager: LocationServiceManager
    //val geoCodingApi: GeocodingApi //TODO WIP
) : ViewModel() {
    private val _activeMarkerMutableState: SnapshotStateList<MarkerData> = mutableStateListOf()
    val activeMarkerState: List<MarkerData> = _activeMarkerMutableState

    private val _activeRouteMutableState: MutableState<Route?> = mutableStateOf(null)
    val activeRouteState: State<Route?> = _activeRouteMutableState

    private val _animateLocationMutableState = MutableStateFlow<LatLngBounds?>(null)
    val animateLocationState: StateFlow<LatLngBounds?> = _animateLocationMutableState

    val savedPlacesFlow: Flow<SavedPlaces> = savedPlacesRepo.savedPlacesFlow
    val savedRoutesFlow: Flow<SavedRoutes> = savedRouteRepo.savedRoutesFlow

    private val _isActiveMarkerOnSavedPlaceMutableState = mutableStateOf(false)
    val isActiveMarkerOnSavedPlaceState: State<Boolean> = _isActiveMarkerOnSavedPlaceMutableState

    private val _isActiveRouteSavedMutableState = mutableStateOf(false)
    val isActiveRouteSavedState: State<Boolean> = _isActiveRouteSavedMutableState

    private val _errorDialogMutableState = mutableStateOf<AppError?>(null)
    val errorDialogState: State<AppError?> = _errorDialogMutableState

    val userPreferences: StateFlow<UserPreferences> = preferencesRepo.userPreferencesFlow.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserPreferences.getDefaultInstance()
    )
    val hasPlacedMarkerOrPolyline: Boolean
        get() = activeMarkerState.isNotEmpty() || activeRouteState.value != null

    val showSaveButton: Boolean
        get() = activeMarkerState.isNotEmpty()


    //Set names for routes dialog
    private val _showOriginAndDestinationNameDialogMutableState = mutableStateOf(false)
    val showOriginAndDestinationNameDialogState: State<Boolean> =
        _showOriginAndDestinationNameDialogMutableState
    private val _showPointNameDialogMutableState = mutableStateOf(false)
    val showPointNameDialogState: State<Boolean> = _showPointNameDialogMutableState

    private val _customOriginName = mutableStateOf("")
    private val _customDestinationName = mutableStateOf("")
    val originName: State<String> = _customOriginName
    val destinationName: State<String> = _customDestinationName
    fun setOriginName(name: String) {
        _customOriginName.value = name
    }

    fun setDestinationName(name: String) {
        _customDestinationName.value = name
    }

    private fun setNamesFromMarkers() {
        _customOriginName.value = activeMarkerState.firstOrNull()?.poi?.name.orEmpty()
        _customDestinationName.value = activeMarkerState.lastOrNull()?.poi?.name.orEmpty()
    }

    fun openSavePlaceDialog() {
        if (activeMarkerState.size >= 2) {
            toggleOriginAndDestinationNameDialog(true)
        } else {
            togglePointNameDialog(true)
        }
    }

    fun toggleOriginAndDestinationNameDialog(show: Boolean) {
        setNamesFromMarkers()
        _showOriginAndDestinationNameDialogMutableState.value = show
    }

    fun togglePointNameDialog(show: Boolean) {
        setNamesFromMarkers()
        _showPointNameDialogMutableState.value = show
    }


    //Searching functionality
    private val _searchResultsMutableState = mutableStateListOf<Place>()
    val searchResultsState: List<Place> = _searchResultsMutableState
    fun searchForPlace(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val request = AutocompletePlacesRequest.newBuilder().setInput(query).build()
                val response = placesStub.autocompletePlaces(request)
                val results = response.toPlaces()
                _searchResultsMutableState.clear()
                _searchResultsMutableState.addAll(results)
            }.onFailure {
                _searchResultsMutableState.clear()
                _errorDialogMutableState.value = AppError(
                    title = "Something went wrong while searching",
                    action = {},
                    dismiss = {})
            }
        }
    }


    fun moveCameraToResultId(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val request = placesStub.getPlace(
                    GetPlaceRequest.newBuilder().setName("places/$id") //TODO Clean
                        .build()
                )
                moveCamera(
                    request.viewport.toLatLngBounds()
                )
            }.onFailure {
                _errorDialogMutableState.value = AppError(
                    title = "Something went wrong",
                    action = {},
                    dismiss = {})
            }
        }
    }



    private suspend fun saveIndividualPlace() {
        val marker = activeMarkerState.first()
        val place = marker.poi?.let { pointOfInterest ->
            Place.newBuilder().setId(pointOfInterest.placeId).setLocation(
                com.google.type.LatLng.newBuilder().setLatitude(pointOfInterest.latLng.latitude)
                    .setLongitude(pointOfInterest.latLng.longitude).build()
            ).setName(pointOfInterest.name).build()
        } ?: Place.newBuilder().setName(_customOriginName.value).setLocation(
            com.google.type.LatLng.newBuilder().setLatitude(marker.latLng.latitude)
                .setLongitude(marker.latLng.longitude).build()
        ).build()
        savedPlacesRepo.addNewPlace(place)
    }

    private suspend fun saveRoute() {
        val route = SavedRoute.newBuilder().setRoute(activeRouteState.value)
            .setOriginName(_customOriginName.value).setDestinationName(_customDestinationName.value)
            .addAllVisitDestinations(activeMarkerState.map {
                it.latLng.toMarkerData().toGmsLatLng()
            }).build()
        savedRouteRepo.addNewRoute(route)
    }

    fun saveActivePlaceOrRoute() {
        viewModelScope.launch {
            if (activeRouteState.value != null) { //If it's a route save
                saveRoute()
            } else { //If it's not a route save and is a place save instead
                saveIndividualPlace()
            }
            _isActiveMarkerOnSavedPlaceMutableState.value = true
        }
    }

    fun deletePlace() {
        viewModelScope.launch {
            //TODO Make this cleaner
            _isActiveMarkerOnSavedPlaceMutableState.value = false
            savedPlacesRepo.deletePlaceById(activeMarkerState[0].poi!!.placeId)
        }
    }

    fun addMarker(markerData: MarkerData) {
        //_polyLineState.clear()
        viewModelScope.launch {
            markerData.poi.let { pointOfInterest ->
                _isActiveMarkerOnSavedPlaceMutableState.value =
                    savedPlacesRepo.checkIfPlaceExists(pointOfInterest?.placeId) //TODO Don't assert
            }
            _activeMarkerMutableState.add(markerData)
            if (activeMarkerState.size >= 2) {
                routeSearch()
            }
        }
    }

    fun removeMarker(markerData: MarkerData) {
        _activeMarkerMutableState.remove(markerData)
    }

    fun clearAllMarkers() {
        _activeMarkerMutableState.clear()
        if (serviceManager.serviceState.value !is LocationMockServiceState.MockingLocation) {
            _activeRouteMutableState.value = null
        }
    }

    fun moveCamera(location: LatLngBounds) {
        _animateLocationMutableState.value = location
    }


    @OptIn(ExperimentalPermissionsApi::class)
    fun stopSpoofing() {
        serviceManager.stopMocking()
        serviceManager.stopAndUnbindService()
    }

    fun routeSearch() {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val response = routeStub.computeRoutes(
                    ComputeRoutesRequest.newBuilder()
                        .setOrigin(activeMarkerState.first().toWaypoint())
                        .setDestination(activeMarkerState.last().toWaypoint()).addAllIntermediates(
                            activeMarkerState.subList(1, activeMarkerState.size - 1)
                                .map { it.toWaypoint() }).build()
                )
                _activeRouteMutableState.value = response.getRoutes(0)
                //_polyLineState.clear()
                //_polyLineState.addAll(decodePolyline(response.routes[0].polyline.encodedPolyline))
            }.onFailure {
                _errorDialogMutableState.value = AppError(
                    title = "Something went wrong while searching for route",
                    action = {},
                    dismiss = {})
            }

        }
    }

    fun setActiveRoute(route: Route) {
        _activeRouteMutableState.value = route
    }


    //TODO CLEAN THIS
    @SuppressLint("InlinedApi")
    @OptIn(ExperimentalPermissionsApi::class)
    fun startMockingLocation(
        navigateImidietly: Route? = null
    ) {
        if (false) {
            _errorDialogMutableState.value = AppError(
                title = "Mock permission error",
                message = "You need to set this app as a mock provider in the developer options",
                action = {},
                dismiss = {})
        }
        if (false) {
            _errorDialogMutableState.value = AppError(
                title = "Notification permission error",
                message = "You need to allow this app to post notifications to spoof your location",
                action = {},
                dismiss = {})
        }

        when {
            navigateImidietly != null -> serviceManager.startMocking(navigateImidietly)
            activeRouteState.value != null ->{
                Log.d("MyLog", "iN When start mock")
                serviceManager.startMocking(activeRouteState.value!!)
            }
            else -> serviceManager.startMocking(activeMarkerState.first().latLng)
        }

    }
}