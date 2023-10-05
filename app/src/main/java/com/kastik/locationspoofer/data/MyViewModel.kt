package com.kastik.locationspoofer.data

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalPermissionsApi::class)
class MyViewModel(
    private val repo: DatastoreRepo,
    val placesSessionToken: AutocompleteSessionToken,
    val locationPermissionState: MultiplePermissionsState,
    val notificationPermissionState: PermissionState?,
    val cameraState: CameraPositionState
    ): ViewModel() {

    private val userLatLng: MutableState<LatLng?> = mutableStateOf(null)
    private val userAlt: MutableState<Double?> = mutableStateOf(null)

    private val currentlySpoofing = mutableStateOf(false)
    private val spoofedLatLng = mutableStateOf<LatLng?>(null)


    private val marker = mutableStateOf<LatLng?>(null)


    private val showLocationErrorDialog = mutableStateOf(false)
    private val showMockPermissionErrorDialog = mutableStateOf(false)
    private val showNotificationErrorDialog = mutableStateOf(false)


    private val isInMapScreen = mutableStateOf(true)

    private val searchActive = mutableStateOf(false)
    private val searchText = mutableStateOf("")

    private val pubResponseNames =  mutableStateListOf<String>()
    private val pubResponseIds = mutableStateListOf<String>()

    private val locationFunctionsEnabled = mutableStateOf(locationPermissionState.permissions[0].status.isGranted || currentlySpoofing.value)

    fun pubResponseNames(): SnapshotStateList<String> {
        return pubResponseNames
    }

    fun pubResponseIds(): SnapshotStateList<String> {
        return pubResponseIds
    }




    fun enableSpoofing() {
        spoofedLatLng.value = marker.value
        currentlySpoofing.value = true
    }

    fun disableSpoofing(){
        spoofedLatLng.value = null
        currentlySpoofing.value = false
    }


    fun setUserLatLng(latLng: LatLng){
        userLatLng.value = latLng
        Log.d("MyLog","setUserLatLng called ${latLng.latitude}")
    }

    fun getUserPosition(): MutableState<LatLng?> {
        return if (spoofedLatLng.value != null) {
            Log.d("MyLog","getMap spoof ${spoofedLatLng.value!!.latitude}")
            spoofedLatLng
        } else {
            Log.d("MyLog","getMap user ${userLatLng.value!!.latitude}")
            userLatLng
        }
    }


    fun getMarker(): MutableState<LatLng?> {
        return marker

    }

    fun setMarker(latLng: LatLng?) {
        this.marker.value = latLng
    }

    fun setUserAlt(position: Double?) {
        this.userAlt.value = position
    }

    fun getUserAlt(): MutableState<Double?>{
        return userAlt
    }


//TODO MAKE ERRORS MORE DYNAMIC
    fun showNotificationPermissionErrorDialog(): MutableState<Boolean>{
        return showNotificationErrorDialog
    }


    fun showNotificationPermissionErrorDialog(showNotificationErrorDialog: Boolean){
        this.showNotificationErrorDialog.value = showNotificationErrorDialog
    }


    fun showMockPermissionErrorDialog(): MutableState<Boolean> {
        return showMockPermissionErrorDialog
    }

    fun showMockPermissionErrorDialog(value :Boolean){
        showMockPermissionErrorDialog.value = value
    }

    fun searchBarMyLocationEnabled(): MutableState<Boolean> {
        return mutableStateOf( locationPermissionState.permissions[0].status.isGranted || locationFunctionsEnabled.value)
    }

    fun showLocationErrorDialog(): MutableState<Boolean> {
        return mutableStateOf(showLocationErrorDialog.value && !locationPermissionState.permissions[0].status.isGranted)
    }

    fun showLocationErrorDialog(showLocationErrorDialog: Boolean) {
        this.showLocationErrorDialog.value = showLocationErrorDialog
    }







    fun searchActive(): MutableState<Boolean>{
        return searchActive
    }

    fun searchText(): MutableState<String>{
        return searchText
    }


    fun isInMapScreen(): MutableState<Boolean>{
        return isInMapScreen
    }

    fun showFab(): MutableState<Boolean>{
        return mutableStateOf(marker.value!=null && isInMapScreen.value)
    }

    fun isExposed(): MutableStateFlow<Boolean> {
        return repo.isExposedEnabled()
    }















    fun mapMyLocationEnabled(): MutableState<Boolean> {

        return mutableStateOf(locationPermissionState.permissions[0].status.isGranted || locationFunctionsEnabled.value)
    }

    fun mapCustomLocationProviderEnabled(): MutableState<Boolean> {
        return mutableStateOf(!locationPermissionState.permissions[0].status.isGranted && currentlySpoofing.value)
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
}