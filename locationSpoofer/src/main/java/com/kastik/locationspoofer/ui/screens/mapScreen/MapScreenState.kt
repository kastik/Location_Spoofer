package com.kastik.locationspoofer.ui.screens.mapScreen

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.kastik.locationspoofer.domain.model.LatLngDomain
import com.kastik.locationspoofer.domain.model.PlaceDomain
import com.kastik.locationspoofer.domain.model.RouteDomain
import com.kastik.locationspoofer.ui.components.DialogState

data class MapScreenUiState(
    val activeRoute: RouteDomain? = null,
    val activePlaces: List<PlaceDomain> = emptyList(),
    val spoofState: SpoofState = SpoofState.Idle,
    val fabState: FabState = FabState(),
    val animateCameraTarget: CameraTarget = CameraTarget.None,
    val searchResults: List<PlaceDomain> = emptyList(),
    val error: AppError? = null,
    val dialogState: DialogState = DialogState.None,
    val savedPlaces: List<PlaceDomain> = emptyList(),
)



//TODO Place these somewhere else
data class AppError(
    val title: String,
    val message: String? = null,
    val action: () -> Unit = {},
    val dismiss: () -> Unit = {}
)

sealed class CameraTarget {
    data class Bounds(val bounds: LatLngBounds) : CameraTarget()
    data class Point(val latLng: LatLng) : CameraTarget()
    data object None : CameraTarget()
}

data class FabState(
    val isSpoofing: Boolean = false,
    val showSpoofButton: Boolean = false,
    val isActiveSaved: Boolean = false,
    val showSaveButton: Boolean = false,
)

sealed class SpoofState {
    object Idle : SpoofState()
    data class Spoofing(
        val latLngDomain: LatLngDomain
    ) : SpoofState()

    data class Failed(val reason: String, val throwable: Throwable? = null) : SpoofState()
    object PermissionMissing : SpoofState()
}
