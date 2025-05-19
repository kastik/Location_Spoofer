package com.kastik.locationspoofer.ui.screens.mapScreen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.kastik.locationspoofer.LocationMockServiceState
import com.kastik.locationspoofer.UpdateLocationService
import com.kastik.locationspoofer.data.api.RetrofitClient
import com.kastik.locationspoofer.data.datastore.SavedPlacesRepo
import com.kastik.locationspoofer.data.datastore.UserPreferencesRepo
import com.kastik.locationspoofer.data.models.MarkerData
import com.kastik.locationspoofer.data.models.mapsAPI.RouteRequest
import com.kastik.locationspoofer.data.models.mapsAPI.RouteResponse
import com.kastik.locationspoofer.data.models.mapsAPI.Waypoint
import com.kastik.locationspoofer.debug.Place
import com.kastik.locationspoofer.debug.SavedPlaces
import com.kastik.locationspoofer.decodePolyline
import com.kastik.locationspoofer.isMockLocationApp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class MapScreenViewModel @Inject constructor(
    val savedPlacesRepo: SavedPlacesRepo,
    val preferencesRepo: UserPreferencesRepo
) : ViewModel() {

    val _mapScreenState: MutableState<MapScreenState> = mutableStateOf(MapScreenState.NoLocation)
    val mapScreenState: State<MapScreenState> = _mapScreenState

    private val _serviceState: MutableState<LocationMockServiceState> =
        mutableStateOf(LocationMockServiceState.Idle)
    val serviceState: State<LocationMockServiceState> = _serviceState

    fun setServiceState(state: LocationMockServiceState) {
        _serviceState.value = state
    }

    init {
        viewModelScope.launch {
            if (!preferencesRepo.userPreferencesFlow.first().askedLocation) {
                _mapScreenState.value = MapScreenState.Error.LocationError
            } else {
                if (preferencesRepo.userPreferencesFlow.first().deniedLocation) {
                    _mapScreenState.value = MapScreenState.DeniedLocation
                } else {
                    _mapScreenState.value = MapScreenState.Location
                }
            }
        }
    }

    val savedPlacesFlow: Flow<SavedPlaces> = savedPlacesRepo.savedPlacesFlow

    private val _markerState: SnapshotStateList<MarkerData> = mutableStateListOf()
    val markerState: List<MarkerData> = _markerState

    private val _polyLineState: SnapshotStateList<LatLng> = mutableStateListOf()
    val polylineState: List<LatLng> = _polyLineState

    private val _isPlaceSaved = MutableStateFlow<Boolean>(false)
    val isPlaceSaved: StateFlow<Boolean> = _isPlaceSaved

    fun hasPlacedMarkerOrPolyline(): Boolean {
        return _markerState.isNotEmpty() || _polyLineState.isNotEmpty()
    }


    fun deniedLocation() {
        viewModelScope.launch {
            preferencesRepo.setDeniedLocationPermission(true)
            preferencesRepo.setAskedLocation(true)
            _mapScreenState.value = MapScreenState.NoLocation
        }
    }

    fun acceptedLocation() {
        viewModelScope.launch {
            preferencesRepo.setDeniedLocationPermission(false)
            preferencesRepo.setAskedLocation(true)
            _mapScreenState.value = MapScreenState.Location
        }
    }

    fun acceptedNotifications() {
        viewModelScope.launch {
            preferencesRepo.setDeniedNotificationPermission(true)
            preferencesRepo.setAskedNotificationPermission(true)
        }
    }

    fun showSaveButton(): Boolean {
        return _markerState.size == 1
    }

    fun savePlace() {
        viewModelScope.launch {

            val place = Place.newBuilder()
                .setPlaceId(_markerState.first().placeId)
                .setLatLng(
                    com.kastik.locationspoofer.debug.LatLng.newBuilder()
                        .setLatitude(_markerState.first().latLng.latitude)
                        .setLongitude(_markerState.first().latLng.longitude)
                )
                .setPlacePrimaryText(_markerState.first().name)
                .build()
            //TODO Make this cleaner
            _isPlaceSaved.value = true
            savedPlacesRepo.addNewPlace(place)
        }
    }

    fun deletePlace() {
        viewModelScope.launch {
            //TODO Make this cleaner
            _isPlaceSaved.value = false
            savedPlacesRepo.deletePlaceById(_markerState[0].placeId.toString())
        }
    }

    fun addMarker(markerData: MarkerData) {
        _polyLineState.clear()
        viewModelScope.launch {
            markerData.let {
                _isPlaceSaved.value =
                    savedPlacesRepo.checkIfPlaceExists(markerData.placeId) //TODO Don't assert
            }
            _markerState.add(markerData)
            if (_markerState.size >= 2) {
                routeSearch()
            }
        }
    }

    fun removeMarker(markerData: MarkerData) {
        _markerState.remove(markerData)
    }

    fun clearAllMarkers() {
        _markerState.clear()
    }

    val animateToLocation = MutableSharedFlow<LatLng?>(extraBufferCapacity = 1)


    fun moveCameraToUser(context: Context) {
        viewModelScope.launch {
            moveCamera(getUserLocation(context))
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    suspend fun getUserLocation(context: Context): LatLng {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        val location = fusedLocationClient.lastLocation.await()
        return LatLng(location.latitude, location.longitude)
    }

    fun moveCamera(location: LatLng) {
        animateToLocation.tryEmit(location)
    }


    @OptIn(ExperimentalPermissionsApi::class)
    fun stopSpoofing(
        binder: UpdateLocationService,
        context: Context,
        hasLocationPermission: Boolean
    ) {
        binder.stopMocking()
        viewModelScope.launch {
            if (preferencesRepo.userPreferencesFlow.first().deniedLocation) {
                _mapScreenState.value = MapScreenState.NoLocation
            } else {
                if (!preferencesRepo.userPreferencesFlow.first().askedLocation) {
                    _mapScreenState.value = MapScreenState.Error.LocationError
                } else {
                    _mapScreenState.value = MapScreenState.Location
                }
            }
        }
    }

    fun routeSearch() {
        viewModelScope.launch {
            val response = searchForRoute(
                startLocation = com.kastik.locationspoofer.data.models.mapsAPI.LatLng(
                    _markerState.first().latLng.latitude, _markerState.first().latLng.longitude
                ), endLocation = com.kastik.locationspoofer.data.models.mapsAPI.LatLng(
                    _markerState.last().latLng.latitude, _markerState.last().latLng.longitude
                ), intermediates = _markerState.subList(1, _markerState.size - 1).map {
                    com.kastik.locationspoofer.data.models.mapsAPI.LatLng(
                        it.latLng.latitude, it.latLng.longitude
                    )
                })
            _polyLineState.clear()
            _polyLineState.addAll(decodePolyline(response.routes[0].polyline.encodedPolyline))
        }
    }

    private suspend fun searchForRoute(
        startLocation: com.kastik.locationspoofer.data.models.mapsAPI.LatLng,
        endLocation: com.kastik.locationspoofer.data.models.mapsAPI.LatLng,
        intermediates: List<com.kastik.locationspoofer.data.models.mapsAPI.LatLng>? = null
    ): RouteResponse {

        val response = RetrofitClient.api.getRoute(
            request = RouteRequest(
                Waypoint(
                    location = com.kastik.locationspoofer.data.models.mapsAPI.Location(
                        startLocation
                    )
                ),
                Waypoint(
                    via = false, location = com.kastik.locationspoofer.data.models.mapsAPI.Location(
                        endLocation,
                    )
                ),
                intermediates = intermediates?.map {
                    Waypoint(
                        location = com.kastik.locationspoofer.data.models.mapsAPI.Location(
                            com.kastik.locationspoofer.data.models.mapsAPI.LatLng(
                                it.latitude, it.longitude
                            )
                        )
                    )
                },
            ), apiKey = TODO(), fieldMask = "*"

        )
        return response
    }


    @SuppressLint("InlinedApi")
    @OptIn(ExperimentalPermissionsApi::class)
    fun startMockingLocation(
        context: Context,
        binder: UpdateLocationService,
        notificationsGranted: Boolean
    ) {
        if (!isMockLocationApp(context)) {
            //TODO Find a way to dismiss it after exiting the intent if user made the change
            _mapScreenState.value = MapScreenState.Error.MockError
        }

        if (notificationsGranted) {
            if (polylineState.isNotEmpty()) {
                binder.setLocation(
                    polylineState
                )
            } else {
                binder.setLocation(
                    _markerState.first().latLng
                )
            }


        }
    }
}