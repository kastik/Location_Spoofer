package com.kastik.locationspoofer.data.mapers

import com.google.android.gms.maps.model.PointOfInterest
import com.google.maps.places.v1.AutocompletePlacesResponse
import com.google.maps.places.v1.Place
import com.google.type.LatLng
import com.kastik.locationspoofer.SavedPlace
import com.kastik.locationspoofer.domain.model.PlaceDomain

fun Place.toPlaceDomain(): PlaceDomain = PlaceDomain(
    id = id,
    name = name,
    viewport = viewport.toViewportDomain(),
    location = location.toDomainLatLng()
)


fun SavedPlace.toPlaceDomain(): PlaceDomain? = this.place?.let {
    PlaceDomain(
        id = it.id,
        name = it.name,
        location = it.location.toDomainLatLng()
    )
}


fun PlaceDomain.toSavedPlaceProto(): SavedPlace =
    SavedPlace.newBuilder().setPlace(
    Place.newBuilder()
        .setId(this.id)
        .setName(this.name)
        .setLocation(
            LatLng.newBuilder()
            .setLatitude(this.location.lat)
            .setLongitude(this.location.lng)
        )
        .build()
    ).build()


fun PointOfInterest.toPlaceDomain(): PlaceDomain = PlaceDomain(
    id = placeId,
    name = name,
    location = latLng.toLatLngDomain()
)

fun com.google.android.gms.maps.model.LatLng.toPlaceDomain(): PlaceDomain = PlaceDomain(
    location = this.toLatLngDomain()
)


fun AutocompletePlacesResponse.toPlacesDomain() =
    suggestionsList.map {
        Place.newBuilder()
            .setName(it.placePrediction.structuredFormat.mainText.text)
            .setId(it.placePrediction.placeId)
            .build()
            .toPlaceDomain()
    }