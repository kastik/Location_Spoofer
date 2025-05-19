package com.kastik.locationspoofer.data.models

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import com.kastik.locationspoofer.debug.Place

data class MarkerData(
    val latLng: LatLng,
    val name: String? = null,
    val placeId: String? = null
)

fun PointOfInterest.toMarkerData(): MarkerData{
    return MarkerData(latLng,name,placeId)
}

fun LatLng.toMarkerData(): MarkerData{
    return MarkerData(this)
}

fun Place.toMarkerData(): MarkerData{
    return MarkerData(
        latLng = LatLng(latLng.latitude,latLng.longitude),
        name = placePrimaryText,
        placeId = placeId
    )
}