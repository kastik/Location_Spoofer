package com.kastik.tests.data.datasource.remote

import com.google.maps.places.v1.AutocompletePlacesRequest
import com.google.maps.places.v1.AutocompletePlacesResponse
import com.google.maps.places.v1.GetPlaceRequest
import com.google.maps.places.v1.Place
import com.google.maps.places.v1.PlacesGrpc
import com.kastik.locationspoofer.data.datasource.remote.PlacesRemoteDataSource
import com.kastik.locationspoofer.data.mapers.toPlaceDomain
import com.kastik.locationspoofer.data.mapers.toPlacesDomain
import com.kastik.locationspoofer.domain.model.PlaceDomain
import kotlin.test.*
import io.mockk.*
import kotlinx.coroutines.test.runTest

class PlacesRemoteDataSourceTest {

    // ⚠️ Adjust if your mapper file has a different compiled name:
    private val mappersClass = "com.kastik.locationspoofer.data.mapers.PlaceMappersKt"

    @BeforeTest fun setup() { mockkStatic(mappersClass) }
    @AfterTest fun tearDown() { unmockkStatic(mappersClass); unmockkAll() }

    @Test
    fun `searchPlaces returns mapped list on success`() = runTest {
        val stub = mockk<PlacesGrpc.PlacesBlockingStub>()
        val ds = PlacesRemoteDataSource(stub)

        val req = AutocompletePlacesRequest.newBuilder().setInput("cafe").build()
        val resp = mockk<AutocompletePlacesResponse>()
        val d0 = mockk<PlaceDomain>()
        val d1 = mockk<PlaceDomain>()

        every { stub.autocompletePlaces(req) } returns resp
        every { resp.toPlacesDomain() } returns listOf(d0, d1)

        val out = ds.searchPlaces("cafe")
        assertEquals(listOf(d0, d1), out)
    }

    @Test
    fun `searchPlaces returns emptyList on error`() = runTest {
        val stub = mockk<PlacesGrpc.PlacesBlockingStub>()
        val ds = PlacesRemoteDataSource(stub)

        every { stub.autocompletePlaces(any()) } throws RuntimeException("boom")

        val out = ds.searchPlaces("x")
        assertTrue(out.isEmpty())
    }

    @Test
    fun `getPlaceDetailsWithId returns mapped on success`() = runTest {
        val stub = mockk<PlacesGrpc.PlacesBlockingStub>()
        val ds = PlacesRemoteDataSource(stub)

        val req = GetPlaceRequest.newBuilder().setName("places/abc").build()
        val placeProto = mockk<Place>()
        val domain = mockk<PlaceDomain>()

        every { stub.getPlace(req) } returns placeProto
        every { placeProto.toPlaceDomain() } returns domain

        val out = ds.getPlaceDetailsWithId("abc")
        assertEquals(domain, out)
    }

    @Test
    fun `getPlaceDetailsWithId returns null on error`() = runTest {
        val stub = mockk<PlacesGrpc.PlacesBlockingStub>()
        val ds = PlacesRemoteDataSource(stub)

        every { stub.getPlace(any()) } throws IllegalStateException("nope")

        val out = ds.getPlaceDetailsWithId("abc")
        assertNull(out)
    }
}
