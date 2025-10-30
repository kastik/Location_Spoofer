package com.kastik.locationspoofer.domain.usecase

import com.kastik.locationspoofer.domain.model.LatLngDomain
import com.kastik.locationspoofer.domain.model.RouteDomain
import com.kastik.locationspoofer.domain.repository.SpoofRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class StartSpoofingUseCase(
    private val repo: SpoofRepository
) {
    operator fun invoke(latLngDomain: LatLngDomain) {
        repo.spoofLocation(latLngDomain)
    }

    operator fun invoke(routeDomain: RouteDomain, loopOnFinish: Boolean,resetOnFinish: Boolean) {
        repo.spoofLocation(routeDomain, loopOnFinish, resetOnFinish)
    }
}

class StopSpoofingUseCase(
    private val repo: SpoofRepository
) {
    operator fun invoke() {
        repo.stopSpoofing()
    }
}

class GetSpoofingStateUseCase(
    private val repo: SpoofRepository
) {
    operator fun invoke() = repo.spoofState

}

class EmulateLatLngUseCase {
    operator fun invoke(
        latLng: LatLngDomain,
        updateIntervalMs: Long = 1000L
    ): Flow<LatLngDomain> = flow {
        emit(latLng)
        delay(updateIntervalMs)
    }
}

class EmulateRouteUseCase(
    private val routeMath: RouteMath = RouteMath,
) {

    operator fun invoke(
        route: RouteDomain,
        updateIntervalMs: Long = 1000L,
        loopOnFinish: Boolean = false,
        resetOnFinish: Boolean = false
    ): Flow<LatLngDomain> = flow {
        val path = decodePolyline(route.encodedPolyline)
        if (path.isEmpty()) {
            return@flow
        }

        var currentIndex = 0
        var currentPoint = path[0]
        val speedMps = 1000.0

        while (true) {
            val nextIndex = currentIndex + 1

            // End of route reached
            if (nextIndex >= path.size) {
                when {
                    loopOnFinish -> {
                        // Restart from beginning
                        currentIndex = 0
                        currentPoint = path[0]
                        continue
                    }
                    resetOnFinish -> {
                        // Reset to start and stop
                        emit(path.first())
                        break
                    }
                    else -> {
                        // Keep emitting last point forever
                        while (true) {
                            emit(currentPoint)
                            delay(updateIntervalMs)
                        }
                    }
                }
            }

            val nextPoint = path[nextIndex]
            val newPoint = routeMath.nextPointAlongSegment(
                currentPoint, nextPoint, speedMps, updateIntervalMs
            )

            emit(newPoint)

            currentPoint = if (newPoint == nextPoint) {
                currentIndex++
                nextPoint
            } else {
                newPoint
            }

            delay(updateIntervalMs)
        }
    }

    private fun decodePolyline(encoded: String): List<LatLngDomain> {
        val poly = mutableListOf<LatLngDomain>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val latD = lat / 1E5
            val lngD = lng / 1E5
            poly.add(LatLngDomain(latD, lngD))
        }

        return poly
    }
}



object RouteMath {
    private const val EARTH_RADIUS_METERS = 6371000.0

    /** Great-circle distance between two points (Haversine formula). */
    fun distanceMeters(a: LatLngDomain, b: LatLngDomain): Double {
        val dLat = Math.toRadians(b.lat - a.lat)
        val dLng = Math.toRadians(b.lng - a.lng)
        val lat1 = Math.toRadians(a.lat)
        val lat2 = Math.toRadians(b.lat)
        val h = sin(dLat / 2).pow(2.0) + sin(dLng / 2).pow(2.0) * cos(lat1) * cos(lat2)
        return 2 * EARTH_RADIUS_METERS * asin(sqrt(h))
    }

    /** Linear interpolation between two coordinates. */
    fun interpolate(a: LatLngDomain, b: LatLngDomain, fraction: Double): LatLngDomain {
        val lat = a.lat + (b.lat - a.lat) * fraction
        val lng = a.lng + (b.lng - a.lng) * fraction
        return LatLngDomain(lat, lng)
    }

    /** Compute the next coordinate along a route, given speed and interval. */
    fun nextPointAlongSegment(
        current: LatLngDomain, next: LatLngDomain, speedMps: Double, updateIntervalMs: Long
    ): LatLngDomain {
        val segmentDistance = distanceMeters(current, next)
        val distanceToTravel = speedMps * (updateIntervalMs / 1000.0)

        return if (segmentDistance <= distanceToTravel) {
            next
        } else {
            val fraction = distanceToTravel / segmentDistance
            interpolate(current, next, fraction)
        }
    }
}
