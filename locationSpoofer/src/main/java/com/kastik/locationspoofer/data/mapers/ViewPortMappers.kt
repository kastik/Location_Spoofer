package com.kastik.locationspoofer.data.mapers

import com.google.geo.type.Viewport
import com.kastik.locationspoofer.domain.model.ViewPortDomain

fun Viewport.toViewportDomain(): ViewPortDomain =
    ViewPortDomain(
        low = low.toDomainLatLng(),
        high = high.toDomainLatLng()
    )


fun ViewPortDomain.toViewport(): Viewport =
    Viewport.newBuilder()
        .setLow(low.toGoogleTypeLatLng())
        .setHigh(high.toGoogleTypeLatLng())
        .build()
