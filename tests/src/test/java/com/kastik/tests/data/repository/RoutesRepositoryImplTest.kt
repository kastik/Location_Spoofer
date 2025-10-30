package com.kastik.tests.data.repository

import com.google.type.LatLng
import com.kastik.locationspoofer.SavedRoute
import com.kastik.locationspoofer.SavedRoutes
import com.kastik.locationspoofer.data.datasource.local.RouteLocalDataSource
import com.kastik.locationspoofer.data.datasource.remote.RoutesRemoteDataSource
import com.kastik.locationspoofer.data.mapers.toRouteDomain
import com.kastik.locationspoofer.data.mapers.toRouteProto
import com.kastik.locationspoofer.data.repository.RoutesRepositoryImpl
import com.kastik.locationspoofer.domain.model.LatLngDomain
import com.kastik.locationspoofer.domain.model.PlaceDomain
import com.kastik.locationspoofer.domain.model.RouteDomain
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.unmockkStatic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RoutesRepositoryImplTest {

    private val mappersClass = "com.kastik.locationspoofer.data.mapers.RouteMappersKt"
    private val latLngMappers = "com.kastik.locationspoofer.data.mapers.LatLngMappersKt"

    @BeforeTest
    fun setup() {
        mockkStatic(mappersClass)
        mockkStatic(latLngMappers)
    }

    @AfterTest
    fun tearDown() {
        unmockkStatic(mappersClass)
        unmockkAll()
    }

    @Test
    fun `savedRoutes maps from local flow`() = runTest {
        val local = mockk<RouteLocalDataSource>()
        val remote = mockk<RoutesRemoteDataSource>()
        val repo = RoutesRepositoryImpl(local, remote)

        val r0 = mockk<SavedRoute>(relaxed = true)
        val r1 = mockk<SavedRoute>(relaxed = true)
        val protoFlow = MutableStateFlow(
            SavedRoutes.newBuilder().addRoutes(r0).addRoutes(r1).build()
        )
        every { local.savedRoutesFlow } returns protoFlow

        val d0 = mockk<RouteDomain>(relaxed = true)
        val d1 = mockk<RouteDomain>(relaxed = true)
        every { r0.toRouteDomain() } returns d0
        every { r1.toRouteDomain() } returns d1

        val list = repo.savedRoutes.first()
        assertEquals(listOf(d0, d1), list)
    }

    @Test
    fun `saveRoute builds proto and calls local`() = runTest {
        val local = mockk<RouteLocalDataSource>(relaxed = true)
        val remote = mockk<RoutesRemoteDataSource>()
        val repo = RoutesRepositoryImpl(local, remote)

        val domain = RouteDomain(
            encodedPolyline = "_p~iF~ps|U_ulLnnqC_mqNvxq`@",
            origin = "Home",
            destination = "Work",
            waypoints = emptyList(),
            nickName = "Commute",
            loop = false,
            speed = 1.5
        )

        coEvery { local.saveRoute(any()) } just Runs

        repo.saveRoute(domain)

        coVerify {
            local.saveRoute(withArg { saved ->
                assertEquals("Home", saved.originName)
                assertEquals("Work", saved.destinationName)
                assertTrue(saved.route.polyline.encodedPolyline.contains("_p~iF"))
            })
        }
    }

    @Test
    fun `computeRoute returns from remote`() = runTest {
        val local = mockk<RouteLocalDataSource>()
        val remote = mockk<RoutesRemoteDataSource>()
        val repo = RoutesRepositoryImpl(local, remote)

        val place1 = mockk<PlaceDomain>(relaxed = true)
        val place2 = mockk<PlaceDomain>(relaxed = true)
        val expected = mockk<RouteDomain>(relaxed = true)

        coEvery { remote.computeRoute(listOf(place1, place2)) } returns expected

        val result = repo.computeRoute(listOf(place1, place2))
        assertEquals(expected, result)
    }

    @Test
    fun `computeRoute uses fallback when remote fails`() = runTest {
        val local = mockk<RouteLocalDataSource>()
        val remote = mockk<RoutesRemoteDataSource>()
        val repo = RoutesRepositoryImpl(local, remote)

        val place = mockk<PlaceDomain>()
        val fakeLatLng = mockk<LatLng>()
        val domainLatLng = LatLngDomain(37.4, -122.0)

        every { place.location } returns domainLatLng
        every { place.location } returns domainLatLng
        coEvery { remote.computeRoute(any()) } returns null

        val result = repo.computeRoute(listOf(place))
        assertEquals(domainLatLng, result.waypoints.first())
        assertEquals("", result.encodedPolyline)
        assertEquals(domainLatLng, result.waypoints.first())
        assertFalse(result.loop)
        assertEquals(0.5, result.speed)
    }

    @Test
    fun `deleteRoute delegates to local with toRouteProto`() = runTest {
        val local = mockk<RouteLocalDataSource>(relaxed = true)
        val remote = mockk<RoutesRemoteDataSource>()
        val repo = RoutesRepositoryImpl(local, remote)

        val domain = mockk<RouteDomain>(relaxed = true)
        val proto = mockk<SavedRoute>(relaxed = true)
        every { domain.toRouteProto() } returns proto

        repo.deleteRoute(domain)
        coVerify { local.deleteRoute(proto) }
    }

    @Test
    fun `checkIfRouteIsSaved delegates to local`() = runTest {
        val local = mockk<RouteLocalDataSource>()
        val remote = mockk<RoutesRemoteDataSource>()
        val repo = RoutesRepositoryImpl(local, remote)

        val domain = mockk<RouteDomain>(relaxed = true)
        val proto = mockk<SavedRoute>(relaxed = true)
        every { domain.toRouteProto() } returns proto
        coEvery { local.isRouteSaved(proto) } returns true

        val result = repo.checkIfRouteIsSaved(domain)
        assertTrue(result)
    }

    @Test
    fun `updateRoute delegates to local`() = runTest {
        val local = mockk<RouteLocalDataSource>(relaxed = true)
        val remote = mockk<RoutesRemoteDataSource>()
        val repo = RoutesRepositoryImpl(local, remote)

        val domain = mockk<RouteDomain>(relaxed = true)
        val proto = mockk<SavedRoute>(relaxed = true)
        every { domain.toRouteProto() } returns proto

        repo.updateRoute(domain)
        coVerify { local.updateRoute(proto) }
    }
}
