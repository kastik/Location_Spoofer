package com.kastik.locationspoofer.data.datasource.local

import androidx.datastore.core.DataStore
import com.google.geo.type.Viewport
import com.google.maps.places.v1.Place
import com.kastik.locationspoofer.SavedPlace
import com.kastik.locationspoofer.SavedPlaces
import com.kastik.locationspoofer.data.mapers.toGoogleTypeLatLng
import com.kastik.locationspoofer.domain.model.LatLngDomain
import com.kastik.locationspoofer.domain.model.ViewPortDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.*

private class FakeDataStore<T>(initial: T) : DataStore<T> {
    private val state = MutableStateFlow(initial)
    override val data = state
    override suspend fun updateData(transform: suspend (t: T) -> T): T {
        val newData = transform(state.value)
        state.value = newData
        return newData
    }
}

private class MockData {

    private fun newSavedPlace(
        id: String,
        name: String,
        customName: String,
        primaryType: String,
        viewPort: ViewPortDomain,
        location: LatLngDomain,

        ): SavedPlace {
        val viewPortProto =
            Viewport.newBuilder()
                .setLow(viewPort.low.toGoogleTypeLatLng())
                .setHigh(viewPort.high.toGoogleTypeLatLng())
                .build()


        val placeProto = Place.newBuilder()
            .setId(id)
            .setName(name)
            .setLocation(location.toGoogleTypeLatLng())
            .setPrimaryType(primaryType)
            .setViewport(viewPortProto)
            .build()

        return SavedPlace.newBuilder()
            .setNickname(customName)
            .setPlace(placeProto)
            .build()
    }

    val viewPort = ViewPortDomain(
        low = LatLngDomain(35.0, 23.0),
        high = LatLngDomain(36.0, 24.0)
    )

    val location = LatLngDomain(
        lat = 37.4219983,
        lng = -122.084,
    )

    val home = newSavedPlace(
        id = "id-1\n\t😀",
        name = "Home 🏡\nSweet\nHome",
        customName = "My\nHouse\r\n✨",
        primaryType = "residential_area\nType",
        viewPort = viewPort,
        location = location
    )

    val office = newSavedPlace(
        id = "id-2",
        name = "Work \"HQ\"\n\tAthens",
        customName = "Main Office 🚀",
        primaryType = "commercial_area",
        viewPort = viewPort,
        location = LatLngDomain(40.6401, 22.9444)
    )

    val factory = newSavedPlace(
        id = "id-3",
        name = "Industrial Complex " + "A".repeat(300),
        customName = "Factory 😎\n\nProduction Site",
        primaryType = "industrial_area",
        viewPort = viewPort,
        location = LatLngDomain(41.085, 23.541)
    )

    val emptySavedPlaces = SavedPlaces
        .newBuilder()
        .build()
    val mockSavedPlaces =
        SavedPlaces
            .newBuilder()
            .addPlace(home)
            .addPlace(office)
            .addPlace(factory)
            .build()


}


class PlacesLocalDataSourceTest {
    private val mockData = MockData()

    @Test
    fun testPlaceAddition() = runTest {
        val dataStore = FakeDataStore(mockData.emptySavedPlaces)
        val local = PlacesLocalDataSource(dataStore)

        local.savePlace(mockData.home)
        local.savePlace(mockData.office)
        local.savePlace(mockData.factory)

        val result = dataStore.data.first()
        assertEquals(3, result.placeCount)

        val first = result.getPlace(0)
        val second = result.getPlace(1)
        val third = result.getPlace(2)

        // --- Home ---
        assertTrue(first.place.id.contains("😀"))
        assertTrue(first.place.name.contains("\nSweet"))
        assertTrue(first.nickname.contains("\r\n"))
        assertEquals("residential_area\nType", first.place.primaryType)
        assertEquals(37.4219983, first.place.location.latitude, 0.0001)
        assertEquals(-122.084, first.place.location.longitude, 0.0001)

        // --- Office ---
        assertEquals("id-2", second.place.id)
        assertTrue(second.place.name.contains("\"HQ\""))
        assertTrue(second.nickname.contains("🚀"))
        assertEquals("commercial_area", second.place.primaryType)
        assertEquals(40.6401, second.place.location.latitude, 0.0001)
        assertEquals(22.9444, second.place.location.longitude, 0.0001)

        // --- Factory ---
        assertEquals("id-3", third.place.id)
        assertTrue(third.place.name.length > 100)
        assertTrue(third.nickname.contains("😎"))
        assertTrue(third.nickname.contains("\n\n"))
        assertEquals("industrial_area", third.place.primaryType)
        assertEquals(41.085, third.place.location.latitude, 0.0001)
        assertEquals(23.541, third.place.location.longitude, 0.0001)
    }


    @Test
    fun testPlaceRemoval() = runTest {
        val dataStore = FakeDataStore(
            SavedPlaces.newBuilder()
                .addPlace(mockData.home)
                .addPlace(mockData.office)
                .addPlace(mockData.factory)
                .build()
        )
        val local = PlacesLocalDataSource(dataStore)

        local.deletePlace(mockData.home)

        val result = dataStore.data.first()
        assertEquals(2, result.placeCount)
        assertEquals(mockData.office.place.id, result.getPlace(0).place.id)
    }

    @Test
    fun testIsPlaceSaved() = runTest {
        val dataStore = FakeDataStore(
            SavedPlaces.newBuilder()
                .addPlace(mockData.home)
                .build()
        )
        val local = PlacesLocalDataSource(dataStore)

        assertTrue(local.isPlaceSaved(mockData.home))
        assertFalse(local.isPlaceSaved(mockData.office))
    }

    @Test
    fun testUpdatePlace() = runTest {
        val dataStore = FakeDataStore(
            SavedPlaces.newBuilder()
                .addPlace(mockData.home)
                .build()
        )
        val local = PlacesLocalDataSource(dataStore)

        val updated = mockData.home.toBuilder().setNickname("newNickName").build()
        local.updatePlace(updated)

        val result = dataStore.data.first()
        assertEquals(1, result.placeCount)
        assertEquals("newNickName", result.getPlace(0).nickname)
    }
}
