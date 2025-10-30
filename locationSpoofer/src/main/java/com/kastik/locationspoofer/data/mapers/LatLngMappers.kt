package com.kastik.locationspoofer.data.mapers

import com.google.android.gms.maps.model.LatLng
import com.google.maps.geocoding.v1.GeocodingResourcesProto
import com.kastik.locationspoofer.domain.model.LatLngDomain

fun LatLng.toLatLngDomain(): LatLngDomain = LatLngDomain(
    this.latitude,
    this.longitude
)

fun GeocodingResourcesProto.LatLng.toLatLngDomain(): LatLngDomain = LatLngDomain(
    this.latitude,
    this.longitude
)

fun LatLngDomain.toGmsLatLng(): LatLng = LatLng(
    lat,
    lng
)

fun LatLngDomain.toGoogleTypeLatLng(): com.google.type.LatLng =
    com.google.type.LatLng.newBuilder().setLatitude(lat).setLongitude(lng).build()

fun com.google.type.LatLng.toDomainLatLng() = LatLngDomain(
    lat = latitude,
    lng = longitude
)