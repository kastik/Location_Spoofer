package com.kastik.locationspoofer.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState

class MyViewModel(): ViewModel() {
    private val userLatLng: MutableState<LatLng> = mutableStateOf(LatLng(0.0,0.0))
    private val userAlt = mutableIntStateOf(0)

    private val currentlySpoofing = mutableStateOf(false)
    private val spoofedLatLng = mutableStateOf<LatLng?>(null)


    private val marker = mutableStateOf<LatLng?>(null)
    private lateinit var cameraState: MutableState<CameraPositionState>

    private val locationPermissionGranted = mutableStateOf(false)


    private val locationFunctionsEnabled = mutableStateOf(false)
    private val showLocationErrorDialog = mutableStateOf(false)
    private val showMockPermissionErrorDialog = mutableStateOf(false)


    fun setLocationPermissionGranted(locationPermissionGranted: Boolean){
        this.locationPermissionGranted.value = locationPermissionGranted
    }

    fun setUserLatLng(latLng: LatLng){
        userLatLng.value = latLng
    }


    fun enableSpoofing() {
        spoofedLatLng.value = marker.value
        currentlySpoofing.value = true
        locationFunctionsEnabled.value = true
    }

    fun disableSpoofing(){
        spoofedLatLng.value = null
        currentlySpoofing.value = false
        if (!locationPermissionGranted.value){
            locationFunctionsEnabled.value = false
        }
    }



    fun showMockPermissionErrorDialog(): MutableState<Boolean> {
        return showMockPermissionErrorDialog
    }



    fun getMapPosition(): MutableState<out LatLng?> {
        return if (spoofedLatLng.value != null) {
            mutableStateOf(spoofedLatLng.value)
        } else {
            userLatLng
        }
    }


    fun searchBarMyLocationEnabled(): MutableState<Boolean> {
        return mutableStateOf( locationPermissionGranted.value || locationFunctionsEnabled.value)
    }

    fun mapMyLocationEnabled(): MutableState<Boolean> {
        return mutableStateOf(locationPermissionGranted.value || locationFunctionsEnabled.value)
    }

    fun mapCustomLocationProviderEnabled(): MutableState<Boolean> {
        return mutableStateOf(!locationPermissionGranted.value && locationFunctionsEnabled.value)
    }

    fun getSpoofedLatLng(): MutableState<LatLng> {
        return mutableStateOf(spoofedLatLng.value!!)
    }


    fun floatingIconStart(): MutableState<Boolean> {
        return if (currentlySpoofing.value && marker.value == spoofedLatLng.value) {
            mutableStateOf(false)
        }else {
            if (currentlySpoofing.value && marker.value != spoofedLatLng.value) {
                mutableStateOf(true)
            }else{
                mutableStateOf(true)
            }
        }
    }



    fun showLocationErrorDialog(): MutableState<Boolean> {
        return showLocationErrorDialog
    }

    fun showLocationErrorDialog(showLocationErrorDialog: Boolean) {
        this.showLocationErrorDialog.value = showLocationErrorDialog
    }


    fun getMarker(): MutableState<LatLng?> {
        return marker

    }

    fun setMarker(latLng: LatLng?) {
        this.marker.value = latLng
    }

    fun getCameraState(): MutableState<CameraPositionState> {
        return cameraState
    }

    fun setCameraState(cameraState: CameraPositionState) {
        this.cameraState = mutableStateOf(cameraState)
    }


    fun userAlt(position: Int) {
        this.userAlt.intValue = position
    }





}