package com.kastik.locationspoofer.ui.screens.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.kastik.locationspoofer.UpdateLocationService
import com.kastik.locationspoofer.data.datastore.SavedPlacesRepo
import com.kastik.locationspoofer.data.datastore.UserPreferencesRepo
import com.kastik.locationspoofer.data.models.MarkerData
import com.kastik.locationspoofer.debug.Place
import com.kastik.locationspoofer.debug.SavedPlaces
import com.kastik.locationspoofer.debug.UserPreferences
import com.kastik.locationspoofer.isMockLocationApp
import com.kastik.locationspoofer.ui.screens.main.components.map.MapScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@OptIn(ExperimentalPermissionsApi::class)
@HiltViewModel
class MainUIViewModel @OptIn(ExperimentalPermissionsApi::class)
@Inject constructor(
    private val savedPlacesRepo: SavedPlacesRepo,
    private val preferencesRepo: UserPreferencesRepo
) : ViewModel() {

    private val _preferencesState =
        MutableStateFlow<UserPreferences>(UserPreferences.newBuilder().build())
    val preferencesState: StateFlow<UserPreferences?> = _preferencesState

    private val _isPlaceSaved = MutableStateFlow<Boolean>(false)
    val isPlaceSaved: StateFlow<Boolean> = _isPlaceSaved


    init {
        viewModelScope.launch {
            preferencesRepo.userPreferencesFlow.collect {
                _preferencesState.value = it
            }
        }
    }

    private val _markerState: MutableState<MarkerData?> = mutableStateOf(null)
    val markerState: State<MarkerData?> = _markerState

    val savedPlacesFlow: Flow<SavedPlaces> = savedPlacesRepo.savedPlacesFlow
    val preferencesFlow = savedPlacesRepo.savedPlacesFlow

    val animateToLocation = MutableSharedFlow<LatLng?>(extraBufferCapacity = 1)

    private val _mapScreenState: MutableState<MapScreenState> =
        mutableStateOf(MapScreenState.NoLocation)
    val mapScreenState: State<MapScreenState> = _mapScreenState


    fun updateMapScreenState(mapScreenState: MapScreenState) {
        _mapScreenState.value = mapScreenState
    }

    fun moveCamera(location: LatLng) {
        animateToLocation.tryEmit(location)
    }

    fun changeMarker(markerData: MarkerData?) {
        viewModelScope.launch {
            markerData?.let {
                _isPlaceSaved.value = savedPlacesRepo.checkIfPlaceExists(markerData.placeId) //TODO Don't assert
            }
            _markerState.value = markerData
        }
    }

    fun savePlace(place: Place) {
        viewModelScope.launch {
            //TODO Make this cleaner
            _isPlaceSaved.value = true
            savedPlacesRepo.addNewPlace(place)
        }
    }

    fun deletePlace() {
        viewModelScope.launch {
            //TODO Make this cleaner
            _isPlaceSaved.value = false
            savedPlacesRepo.deletePlaceById(_markerState.value!!.placeId!!)
        }
    }

    fun setDeniedLocation(value: Boolean) {
        viewModelScope.launch {
            preferencesRepo.setDeniedLocationPermission(value)
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    fun stopSpoofing(
        context: Context,
        locationPermissionState: MultiplePermissionsState,
    ) {
        val intent = Intent(context, UpdateLocationService::class.java)
        context.stopService(intent)
        viewModelScope.launch {
            getInitialState(context, locationPermissionState)
        }
    }

    @SuppressLint("InlinedApi")
    @OptIn(ExperimentalPermissionsApi::class)
    fun startMockingLocation(
        context: Context,
        locationPermissionState: MultiplePermissionsState,
        notificationPermissionState: PermissionState,
    ) {
        if (!isMockLocationApp(context)) {
            //TODO Find a way to dismiss it after exiting the intent if user made the change
            _mapScreenState.value = MapScreenState.Error(
                displayMsg = "You need to select this app as a mock provider to do that!",
                displayTitle = "App is not a mock provider",
                action = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                },
                dismiss = {
                    //TODO
                }
            )
        }

        val intent = Intent(context, UpdateLocationService::class.java)
        context.stopService(intent)

        if (notificationPermissionState.status.isGranted) {
            val intent = Intent(context, UpdateLocationService::class.java).apply {
                putExtra("Latitude", markerState.value!!.latLng.latitude)
                putExtra("Longitude", markerState.value!!.latLng.longitude)
            }
            context.startService(intent)
            _mapScreenState.value = MapScreenState.SpoofingLocation(markerState.value!!.latLng)
        } else {
            _mapScreenState.value = if (_preferencesState.value.deniedNotifications) {
                MapScreenState.Error(
                    displayMsg = "The app won't run if you don't allow notifications!",
                    displayTitle = "Please allow notifications",
                    action = {
                        notificationPermissionState.launchPermissionRequest()
                    },
                    dismiss = {
                        viewModelScope.launch {
                            getInitialState(context, locationPermissionState)
                        }
                    }
                )
            } else {
                MapScreenState.Error(
                    displayMsg = "The app needs to run in the background to spoof your location and it can't do that without a notification" +
                            "Please allow notifications",
                    displayTitle = "Please allow notifications",
                    action = {
                        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                            putExtra(
                                Settings.EXTRA_APP_PACKAGE,
                                "com.kastik.locationspoofer.debug"
                            ) //TODO Make actual call with context
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(intent)
                    },
                    dismiss = {

                    }
                )
            }
        }
    }


    @SuppressLint("MissingPermission")
    @OptIn(ExperimentalPermissionsApi::class)
    suspend fun getInitialState(
        context: Context,
        locationPermissionState: MultiplePermissionsState
    ) {
        _mapScreenState.value = if (locationPermissionState.permissions[0].status.isGranted) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            val location = fusedLocationClient.lastLocation.await()
            MapScreenState.LoadedLocation(LatLng(location.latitude, location.longitude))
        } else {
            if (_preferencesState.value.deniedLocation) {
                MapScreenState.Error(
                    displayMsg = "Your location isn't collected but used to help you navigate the map, the app will still function but it's recommended to turn it on!",
                    displayTitle = "Please allow location",
                    action = {
                        locationPermissionState.launchMultiplePermissionRequest()
                    },
                    dismiss = {
                        setDeniedLocation(true)
                    }
                )
            } else {
                //TODO Save this and don't ask again if denied
                MapScreenState.NoLocation
            }
        }
    }


}
