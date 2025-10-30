package com.kastik.tests.data.datasource.remote

import com.google.maps.routing.v2.ComputeRoutesRequest
import com.google.maps.routing.v2.ComputeRoutesResponse
import com.google.maps.routing.v2.Route
import com.google.maps.routing.v2.RoutesGrpc
import com.kastik.locationspoofer.data.datasource.remote.RoutesRemoteDataSource
import com.kastik.locationspoofer.data.mapers.toRouteDomain
import com.kastik.locationspoofer.data.mapers.toWaypoint
import com.kastik.locationspoofer.domain.model.PlaceDomain
import com.kastik.locationspoofer.domain.model.RouteDomain
import io.mockk.*
import kotlin.test.*
import kotlinx.coroutines.test.runTest

class RoutesRemoteDataSourceTest {

    // ⚠️ Adjust this if your mapper file is compiled under a different name:
    private val mappersClass = "com.kastik.locationspoofer.data.mapers.RouteMappersKt"

    @BeforeTest
    fun setup() {
        mockkStatic(mappersClass)
    }

    @AfterTest
    fun tearDown() {
        unmockkStatic(mappersClass)
        unmockkAll()
    }

    @Test
    fun `computeRoute returns mapped route on success`() = runTest {
        val stub = mockk<RoutesGrpc.RoutesBlockingStub>()
        val ds = RoutesRemoteDataSource(stub)

        // Mocked waypoints
        val p1 = mockk<PlaceDomain>()
        val p2 = mockk<PlaceDomain>()
        val w1 = mockk<com.google.maps.routing.v2.Waypoint>()
        val w2 = mockk<com.google.maps.routing.v2.Waypoint>()

        every { p1.toWaypoint() } returns w1
        every { p2.toWaypoint() } returns w2

        // Mock proto response
        val requestSlot = slot<ComputeRoutesRequest>()
        val resp = mockk<ComputeRoutesResponse>()
        val routeProto = mockk<Route>()
        val domainRoute = mockk<RouteDomain>()

        every { stub.computeRoutes(capture(requestSlot)) } returns resp
        every { resp.getRoutes(0) } returns routeProto
        every { routeProto.toRouteDomain() } returns domainRoute

        val out = ds.computeRoute(listOf(p1, p2))

        // Verify the result
        assertEquals(domainRoute, out)

        // Verify the request was built correctly
        val req = requestSlot.captured
        assertEquals(w1, req.origin)
        assertEquals(w2, req.destination)
        assertTrue(req.intermediatesList.isEmpty())

        verify(exactly = 1) { stub.computeRoutes(any()) }
        verify(exactly = 1) { routeProto.toRouteDomain() }
    }

    @Test
    fun `computeRoute returns null when waypoints is empty`() = runTest {
        val stub = mockk<RoutesGrpc.RoutesBlockingStub>()
        val ds = RoutesRemoteDataSource(stub)

        val out = ds.computeRoute(emptyList())
        assertNull(out)
        verify { stub.computeRoutes(any()) wasNot Called }
    }

    @Test
    fun `computeRoute handles single waypoint gracefully`() = runTest {
        val stub = mockk<RoutesGrpc.RoutesBlockingStub>()
        val ds = RoutesRemoteDataSource(stub)

        val p1 = mockk<PlaceDomain>()
        val w1 = mockk<com.google.maps.routing.v2.Waypoint>()
        every { p1.toWaypoint() } returns w1

        val resp = mockk<ComputeRoutesResponse>()
        val routeProto = mockk<Route>()
        val domainRoute = mockk<RouteDomain>()

        every { stub.computeRoutes(any()) } returns resp
        every { resp.getRoutes(0) } returns routeProto
        every { routeProto.toRouteDomain() } returns domainRoute

        val out = ds.computeRoute(listOf(p1))

        // It should still compute a route even with only 1 waypoint (same as origin=dest)
        assertEquals(domainRoute, out)
        verify { stub.computeRoutes(any()) }
    }

    @Test
    fun `computeRoute returns null on exception`() = runTest {
        val stub = mockk<RoutesGrpc.RoutesBlockingStub>()
        val ds = RoutesRemoteDataSource(stub)

        val p1 = mockk<PlaceDomain>()
        val p2 = mockk<PlaceDomain>()
        val w1 = mockk<com.google.maps.routing.v2.Waypoint>()
        val w2 = mockk<com.google.maps.routing.v2.Waypoint>()

        every { p1.toWaypoint() } returns w1
        every { p2.toWaypoint() } returns w2
        every { stub.computeRoutes(any()) } throws IllegalStateException("boom")

        val out = ds.computeRoute(listOf(p1, p2))
        assertNull(out)

        verify(exactly = 1) { stub.computeRoutes(any()) }
    }
}
