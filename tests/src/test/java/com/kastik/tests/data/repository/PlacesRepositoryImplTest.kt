package com.kastik.tests.data.repository

import com.kastik.locationspoofer.data.datasource.local.PlacesLocalDataSource
import com.kastik.locationspoofer.data.datasource.remote.PlacesRemoteDataSource
import com.kastik.locationspoofer.data.repository.PlacesRepositoryImpl
import com.kastik.locationspoofer.domain.model.PlaceDomain
import kotlin.test.*
import io.mockk.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import com.kastik.locationspoofer.SavedPlace
import com.kastik.locationspoofer.SavedPlaces
import com.kastik.locationspoofer.data.mapers.toPlaceDomain
import com.kastik.locationspoofer.data.mapers.toSavedPlaceProto

class PlacesRepositoryImplTest {

    // ⚠️ CHANGE THIS to match your actual mappers file compiled name:
    private val mappersClass = "com.kastik.locationspoofer.data.mapers.PlaceMappersKt"

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
    fun `savedPlaces maps from local flow`() = runTest {
        val local = mockk<PlacesLocalDataSource>()
        val remote = mockk<PlacesRemoteDataSource>()
        val repo = PlacesRepositoryImpl(local, remote)

        // Local emits proto list
        val p0 = mockk<SavedPlace>(relaxed = true)
        val p1 = mockk<SavedPlace>(relaxed = true)
        val protoFlow = MutableStateFlow(
            SavedPlaces.newBuilder().addPlace(p0).addPlace(p1).build()
        )
        every { local.savedPlacesFlow } returns protoFlow

        // Map each to PlaceDomain via top-level mapper
        val d0 = mockk<PlaceDomain>(relaxed = true)
        val d1 = mockk<PlaceDomain>(relaxed = true)
        every { p0.toPlaceDomain() } returns d0
        every { p1.toPlaceDomain() } returns d1

        val list = repo.savedPlaces.first()
        assertEquals(listOf(d0, d1), list)
    }

    @Test
    fun `savePlace delegates to local with toSavedPlaceProto`() = runTest {
        val local = mockk<PlacesLocalDataSource>(relaxed = true)
        val remote = mockk<PlacesRemoteDataSource>()
        val repo = PlacesRepositoryImpl(local, remote)

        val domain = mockk<PlaceDomain>(relaxed = true)
        val proto = mockk<SavedPlace>(relaxed = true)
        every { domain.toSavedPlaceProto() } returns proto

        repo.savePlace(domain)
        coVerify { local.savePlace(proto) }
    }

    @Test
    fun `deletePlace delegates to local with toSavedPlaceProto`() = runTest {
        val local = mockk<PlacesLocalDataSource>(relaxed = true)
        val remote = mockk<PlacesRemoteDataSource>()
        val repo = PlacesRepositoryImpl(local, remote)

        val domain = mockk<PlaceDomain>(relaxed = true)
        val proto = mockk<SavedPlace>(relaxed = true)
        every { domain.toSavedPlaceProto() } returns proto

        repo.deletePlace(domain)
        coVerify { local.deletePlace(proto) }
    }

    @Test
    fun `updatePlace delegates to local with toSavedPlaceProto`() = runTest {
        val local = mockk<PlacesLocalDataSource>(relaxed = true)
        val remote = mockk<PlacesRemoteDataSource>()
        val repo = PlacesRepositoryImpl(local, remote)

        val domain = mockk<PlaceDomain>(relaxed = true)
        val proto = mockk<SavedPlace>(relaxed = true)
        every { domain.toSavedPlaceProto() } returns proto

        repo.updatePlace(domain)
        coVerify { local.updatePlace(proto) }
    }

    @Test
    fun `checkIfPlaceIsStored delegates to local with toSavedPlaceProto`() = runTest {
        val local = mockk<PlacesLocalDataSource>()
        val remote = mockk<PlacesRemoteDataSource>()
        val repo = PlacesRepositoryImpl(local, remote)

        val domain = mockk<PlaceDomain>(relaxed = true)
        val proto = mockk<SavedPlace>(relaxed = true)
        every { domain.toSavedPlaceProto() } returns proto
        coEvery { local.isPlaceSaved(proto) } returns true

        val result = repo.checkIfPlaceIsStored(domain)
        assertTrue(result)
    }

    @Test
    fun `searchPlaces delegates to remote`() = runTest {
        val local = mockk<PlacesLocalDataSource>()
        val remote = mockk<PlacesRemoteDataSource>()
        val repo = PlacesRepositoryImpl(local, remote)

        val query = "cafe"
        val d0 = mockk<PlaceDomain>(relaxed = true)
        val d1 = mockk<PlaceDomain>(relaxed = true)
        coEvery { remote.searchPlaces(query) } returns listOf(d0, d1)

        val out = repo.searchPlaces(query)
        assertEquals(listOf(d0, d1), out)
    }

    @Test
    fun `getPlaceDetailsWithId delegates to remote`() = runTest {
        val local = mockk<PlacesLocalDataSource>()
        val remote = mockk<PlacesRemoteDataSource>()
        val repo = PlacesRepositoryImpl(local, remote)

        val id = "abc"
        val d = mockk<PlaceDomain>(relaxed = true)
        coEvery { remote.getPlaceDetailsWithId(id) } returns d

        val out = repo.getPlaceDetailsWithId(id)
        assertEquals(d, out)
    }
}
