package com.kastik.locationspoofer.data.mapers

import com.google.maps.routing.v2.Location
import com.google.maps.routing.v2.Polyline
import com.google.maps.routing.v2.Route
import com.google.maps.routing.v2.Waypoint
import com.google.type.LatLng
import com.kastik.locationspoofer.SavedRoute
import com.kastik.locationspoofer.domain.model.PlaceDomain
import com.kastik.locationspoofer.domain.model.RouteDomain

fun SavedRoute.toRouteDomain(): RouteDomain = RouteDomain(
    nickName = nickname,
    origin = originName,
    destination = destinationName,
    encodedPolyline = route.polyline.toString(),
    waypoints = visitDestinationsList.map { it.toDomainLatLng() },
    loop = loop,
    speed = speed,
)

fun Route.toRouteDomain(): RouteDomain = RouteDomain(
    encodedPolyline = polyline.encodedPolyline
)

fun PlaceDomain.toWaypoint(): Waypoint =
    Waypoint.newBuilder()
        .setPlaceId(id)
        .setLocation(
            Location.newBuilder()
                .setLatLng(
                    LatLng.newBuilder()
                        .setLatitude(location.lat)
                        .setLongitude(location.lng)
                        .build()
                ).build()
        )
        .build()

fun RouteDomain.toRouteProto(): SavedRoute = SavedRoute.newBuilder()
    .setNickname(nickName.orEmpty())
    .setOriginName(origin.orEmpty())
    .setDestinationName(destination.orEmpty())
    .setLoop(loop)
    .setSpeed(speed)
    .setRoute(
        Route.newBuilder()
            .setPolyline(
                Polyline.newBuilder()
                    .setEncodedPolyline(encodedPolyline).build()
            ).build()
    ).build()