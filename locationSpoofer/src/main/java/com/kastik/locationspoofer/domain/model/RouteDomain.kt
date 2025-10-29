package com.kastik.locationspoofer.domain.model

data class RouteDomain(
    val nickName: String? = null,
    val origin: String? = null,
    val destination: String? = null,
    val encodedPolyline: String,
    val waypoints: List<LatLngDomain> = emptyList(),
    val loop: Boolean = false,
    val speed: Double = 1.0,
)