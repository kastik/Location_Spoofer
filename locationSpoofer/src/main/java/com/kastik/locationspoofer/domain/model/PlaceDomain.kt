package com.kastik.locationspoofer.domain.model

data class PlaceDomain(
    val id: String? = null,
    val name: String? = null,
    val customName: String? = null,
    val primaryType: String? = null,
    val viewport: ViewPortDomain? = null,
    val location: LatLngDomain
)