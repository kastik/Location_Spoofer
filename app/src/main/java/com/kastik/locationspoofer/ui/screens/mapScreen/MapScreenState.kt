package com.kastik.locationspoofer.ui.screens.mapScreen

sealed class MapScreenState {
    data object NoLocation : MapScreenState()
    data object DeniedLocation : MapScreenState()
    data object Location : MapScreenState()

    sealed class Error : MapScreenState() {
        data object LocationError : Error()
        data object NotificationError : Error()
        data object MockError : Error()
    }
}