package com.kastik.locationspoofer.data.models

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import com.google.maps.places.v1.AutocompletePlacesResponse
import com.google.maps.places.v1.Place
import com.google.maps.routing.v2.Location
import com.google.maps.routing.v2.Polyline
import com.google.maps.routing.v2.Waypoint

data class MarkerData(
    val latLng: LatLng,
    val poi: PointOfInterest? = null,
)
fun MarkerData.toWaypoint(): Waypoint =
    Waypoint.newBuilder()
        .setLocation(
            Location.newBuilder().setLatLng(
                com.google.type.LatLng.newBuilder()
                    .setLatitude(latLng.latitude)
                    .setLongitude(latLng.longitude)
            )
        ).build()

fun PointOfInterest.toMarkerData(): MarkerData{
    return MarkerData(latLng,this)
}

fun LatLng.toMarkerData(): MarkerData{
    return MarkerData(this)
}
fun MarkerData.toGmsLatLng(): com.google.type.LatLng {
    return com.google.type.LatLng.newBuilder()
        .setLatitude(latLng.latitude)
        .setLongitude(latLng.longitude)
        .build()
}

fun com.google.type.LatLng.toGmsLatLng(): LatLng{
    return LatLng(latitude,longitude)
}

fun Place.toGmsLatLng(): LatLng{
    Log.d("MyLog","Long was ${location.longitude} lat was ${location.latitude}")
    return LatLng(location.latitude,location.longitude)
}

fun Place.toMarkerData(): MarkerData{
    return MarkerData(
        latLng = LatLng(location.latitude,location.longitude),
        poi = PointOfInterest(location.toGmsLatLng(),displayName.text,id),
    )
}

fun AutocompletePlacesResponse.toPlaces() =
    suggestionsList.map {
        Place.newBuilder()
            .setName(it.placePrediction.structuredFormat.mainText.text)
            .setId(it.placePrediction.placeId)
            .build()
    }

fun Polyline.decodeToLatLngList(): List<LatLng> {
    if (encodedPolyline.isEmpty()) return emptyList()
    return com.google.maps.android.PolyUtil.decode(encodedPolyline)
}