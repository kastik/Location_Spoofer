package com.kastik.tests.domain

import com.kastik.locationspoofer.domain.model.LatLngDomain
import com.kastik.locationspoofer.domain.usecase.RouteMath
import org.junit.Test
import org.junit.Assert.*

class RouteMathTest {

    @Test
    fun distanceMeters_returnsRoughly111kmFor1DegreeLonAtEquator() {
        val a = LatLngDomain(0.0, 0.0)
        val b = LatLngDomain(0.0, 1.0)
        val d = RouteMath.distanceMeters(a, b)
        assertTrue(d in 110_000.0..112_500.0)
    }

    @Test
    fun interpolate_halfway() {
        val a = LatLngDomain(0.0, 0.0)
        val b = LatLngDomain(10.0, 10.0)
        val mid = RouteMath.interpolate(a, b, 0.5)
        assertEquals(5.0, mid.lat, 1e-4)
        assertEquals(5.0, mid.lng, 1e-4)
    }

    @Test
    fun nextPointAlongSegment_snapsToEndWhenClose() {
        val a = LatLngDomain(0.0, 0.0)
        val b = LatLngDomain(0.0, 0.00001) // ~1.11 m
        val p = RouteMath.nextPointAlongSegment(a, b, speedMps = 1000.0, updateIntervalMs = 10)
        assertEquals(b, p)
    }

    @Test
    fun nextPointAlongSegment_interpolatesWhenFar() {
        val a = LatLngDomain(0.0, 0.0)
        val b = LatLngDomain(1.0, 1.0)
        val p = RouteMath.nextPointAlongSegment(a, b, speedMps = 1.0, updateIntervalMs = 1000)
        assertNotEquals(b, p)
    }
}
