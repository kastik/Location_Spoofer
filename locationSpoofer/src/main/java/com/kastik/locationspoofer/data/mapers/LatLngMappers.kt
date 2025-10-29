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

fun LatLngDomain.toLatLng(): LatLng = LatLng(
    lat,
    lng
)

fun com.google.type.LatLng.toDomainLatLng() = LatLngDomain(
    lat = latitude,
    lng = longitude
)