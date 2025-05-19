package com.kastik.locationspoofer.data.models

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse

data class PlaceResult(
    val placeId : String,
    val placeName: String
)

fun PlaceResult.toLatLng(
    fetchPlace: (FetchPlaceRequest) -> Task<FetchPlaceResponse>,
    callback: (LatLng) -> Unit
){
    val placeFields = listOf(Place.Field.LAT_LNG)
    val request = FetchPlaceRequest.newInstance(placeId, placeFields)

    fetchPlace(request)
        .addOnSuccessListener { response: FetchPlaceResponse ->
            val location = response.place.location
            if (location != null) {
                callback(location) // Use callback to return the result
            }
        }
        .addOnFailureListener { exception ->
            // Handle error here if needed
        }

}
