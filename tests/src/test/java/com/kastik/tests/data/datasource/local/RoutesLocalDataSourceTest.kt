package com.kastik.tests.data.datasource.local

import androidx.datastore.core.DataStore
import com.google.maps.routing.v2.Polyline
import com.google.maps.routing.v2.Route
import com.google.type.LatLng
import com.kastik.locationspoofer.SavedRoute
import com.kastik.locationspoofer.SavedRoutes
import com.kastik.locationspoofer.data.datasource.local.RouteLocalDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private class FakeDataStore<T>(initial: T) : DataStore<T> {
    private val state = MutableStateFlow(initial)
    override val data = state
    override suspend fun updateData(transform: suspend (t: T) -> T): T {
        val newData = transform(state.value)
        state.value = newData
        return newData
    }
}

private class MockRouteData {

    private fun newSavedRoute(
        originName: String,
        destinationName: String,
        route: Route,
        visitDestinations: List<LatLng>,
        loop: Boolean,
        speed: Double,
        nickName: String
    ): SavedRoute {

        return SavedRoute.newBuilder()
            .setOriginName(originName)
            .setDestinationName(destinationName)
            .setRoute(route)
            .addAllVisitDestinations(visitDestinations)
            .setLoop(loop)
            .setSpeed(speed)
            .setNickname(nickName)
            .build()
    }

    private fun latLng(lat: Double, lng: Double): LatLng =
        LatLng.newBuilder().setLatitude(lat).setLongitude(lng).build()

    private fun newRoute(encodedPolyline: String): Route =
        Route.newBuilder()
            .setPolyline(
                Polyline.newBuilder()
                    .setEncodedPolyline(encodedPolyline)
                    .build()
            )
            .build()

    val route1 = newSavedRoute(
        originName = "Home 🏡\nSweet\nHome",
        destinationName = "Office 🚀",
        route = newRoute("_p~iF~ps|U_ulLnnqC_mqNvxq`@"),
        visitDestinations = listOf(
            latLng(37.4219983, -122.084),
            latLng(40.6401, 22.9444)
        ),
        loop = false,
        speed = 15.5,
        nickName = "Morning Commute\n😀"
    )

    val route2 = newSavedRoute(
        originName = "Office HQ\n\tAthens",
        destinationName = "Gym 🏋️‍♂️",
        route = newRoute("abc123xyz\n\r\tencodedPolyline"),
        visitDestinations = listOf(
            latLng(40.0, 23.0),
            latLng(41.0, 24.0),
            latLng(42.0, 25.0)
        ),
        loop = true,
        speed = 30.0,
        nickName = "Workout Route 💪"
    )

    val route3 = newSavedRoute(
        originName = "Vacation Start ✈️",
        destinationName = "Paradise Island 🌴" + "A".repeat(200),
        route = newRoute("polyline" + "Z".repeat(200)),
        visitDestinations = listOf(
            latLng(35.0, 25.0),
            latLng(36.0, 26.0)
        ),
        loop = false,
        speed = 80.0,
        nickName = "Long trip 😎"
    )

    val emptyRoutes = SavedRoutes.newBuilder().build()

    val mockRoutes = SavedRoutes.newBuilder()
        .addRoutes(route1)
        .addRoutes(route2)
        .addRoutes(route3)
        .build()
}


class RouteLocalDataSourceTest {
    private val mockData = MockRouteData()

    @Test
    fun testRouteAddition() = runTest {
        val dataStore = FakeDataStore(mockData.emptyRoutes)
        val local = RouteLocalDataSource(dataStore)

        local.saveRoute(mockData.route1)
        local.saveRoute(mockData.route2)
        local.saveRoute(mockData.route3)

        val result = dataStore.data.first()
        assertEquals(3, result.routesCount)

        val first = result.getRoutes(0)
        val second = result.getRoutes(1)
        val third = result.getRoutes(2)

        // --- Route 1 ---
        assertTrue(first.originName.contains("🏡"))
        assertTrue(first.destinationName.contains("🚀"))
        assertEquals("Morning Commute\n😀", first.nickname)
        assertEquals(15.5, first.speed, 0.01)
        assertEquals(2, first.visitDestinationsCount)
        assertFalse(first.loop)

        // --- Route 2 ---
        assertTrue(second.originName.contains("Athens"))
        assertTrue(second.destinationName.contains("🏋️‍♂️"))
        assertTrue(second.loop)
        assertEquals(30.0, second.speed, 0.01)
        assertEquals(3, second.visitDestinationsCount)
        assertTrue(second.route.polyline.encodedPolyline.contains("encodedPolyline"))

        // --- Route 3 ---
        assertTrue(third.originName.contains("✈️"))
        assertTrue(third.destinationName.length > 200)
        assertTrue(third.nickname.contains("😎"))
        assertEquals(80.0, third.speed, 0.01)
        assertEquals(2, third.visitDestinationsCount)
    }

    @Test
    fun testRouteRemoval() = runTest {
        val dataStore = FakeDataStore(mockData.mockRoutes)
        val local = RouteLocalDataSource(dataStore)

        local.deleteRoute(mockData.route1)

        val result = dataStore.data.first()
        assertEquals(2, result.routesCount)
        assertEquals(mockData.route2.route.polyline.encodedPolyline, result.getRoutes(0).route.polyline.encodedPolyline)
    }

    @Test
    fun testIsRouteSaved() = runTest {
        val dataStore = FakeDataStore(
            SavedRoutes.newBuilder().addRoutes(mockData.route2).build()
        )
        val local = RouteLocalDataSource(dataStore)

        assertTrue(local.isRouteSaved(mockData.route2))
        assertFalse(local.isRouteSaved(mockData.route3))
    }

    @Test
    fun testUpdateRoute() = runTest {
        val dataStore = FakeDataStore(
            SavedRoutes.newBuilder().addRoutes(mockData.route1).build()
        )
        val local = RouteLocalDataSource(dataStore)

        val updated = mockData.route1.toBuilder()
            .setNickname("Updated Nick 🚀")
            .setSpeed(25.0)
            .setLoop(true)
            .build()

        local.updateRoute(updated)

        val result = dataStore.data.first()
        assertEquals(1, result.routesCount)
        val route = result.getRoutes(0)
        assertEquals("Updated Nick 🚀", route.nickname)
        assertEquals(25.0, route.speed, 0.01)
        assertTrue(route.loop)
    }
}