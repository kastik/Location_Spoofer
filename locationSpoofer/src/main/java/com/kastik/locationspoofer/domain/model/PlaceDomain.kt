package com.kastik.locationspoofer.domain.model

import com.google.geo.type.Viewport

data class PlaceDomain(
    val id: String? = null,
    val name: String? = null,
    val customName: String? = null,
    val primaryType: String? = null,
    val viewport: Viewport? = null,
    val location: LatLngDomain
)