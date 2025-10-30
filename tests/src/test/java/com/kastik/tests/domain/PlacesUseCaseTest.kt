package com.kastik.tests.domain

import com.kastik.locationspoofer.domain.model.PlaceDomain
import com.kastik.locationspoofer.domain.repository.PlacesRepository
import com.kastik.locationspoofer.domain.usecase.CheckIfPlaceIsSavedUseCase
import com.kastik.locationspoofer.domain.usecase.DeletePlaceUseCase
import com.kastik.locationspoofer.domain.usecase.GetPlaceDetailsWithId
import com.kastik.locationspoofer.domain.usecase.GetSavedPlacesUseCase
import com.kastik.locationspoofer.domain.usecase.SavePlaceUseCase
import com.kastik.locationspoofer.domain.usecase.SearchPlacesUseCase
import com.kastik.locationspoofer.domain.usecase.UpdatePlaceUseCase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest

private class FakePlacesRepository : PlacesRepository {
    private val _saved = MutableStateFlow<List<PlaceDomain>>(emptyList())
    override val savedPlaces: StateFlow<List<PlaceDomain>> = _saved

    private val searchMap = mutableMapOf<String, List<PlaceDomain>>()
    private val detailsMap = mutableMapOf<String, PlaceDomain?>()

    fun seedSearch(query: String, results: List<PlaceDomain>) { searchMap[query] = results }
    fun seedDetails(id: String, place: PlaceDomain?) { detailsMap[id] = place }

    override suspend fun savePlace(place: PlaceDomain) {
        _saved.value = _saved.value + place
    }
    override suspend fun deletePlace(place: PlaceDomain) {
        _saved.value = _saved.value.filterNot { it == place || it === place }
    }
    override suspend fun checkIfPlaceIsStored(place: PlaceDomain): Boolean {
        return _saved.value.any { it == place || it === place }
    }
    override suspend fun updatePlace(place: PlaceDomain) {
        val list = _saved.value
        val idx = list.indexOfFirst { it == place || it === place }
        _saved.value = if (idx >= 0) list.toMutableList().apply { this[idx] = place } else list + place
    }
    override suspend fun searchPlaces(query: String): List<PlaceDomain> = searchMap[query].orEmpty()
    override suspend fun getPlaceDetailsWithId(id: String): PlaceDomain? = detailsMap[id]
}

class PlacesUseCasesKotlinTest {

    @Test
    fun getSavedPlaces_exposesFlow() = runTest {
        val repo = FakePlacesRepository()
        val getSaved = GetSavedPlacesUseCase(repo)

        assertTrue(getSaved().first().isEmpty())

        val p1 = mockk<PlaceDomain>(relaxed = true)
        repo.savePlace(p1)
        assertEquals(listOf(p1), getSaved().first())

        val p2 = mockk<PlaceDomain>(relaxed = true)
        repo.savePlace(p2)
        assertEquals(listOf(p1, p2), getSaved().first())
    }

    @Test
    fun savePlace_adds() = runTest {
        val repo = FakePlacesRepository()
        val save = SavePlaceUseCase(repo)
        val getSaved = GetSavedPlacesUseCase(repo)

        val p = mockk<PlaceDomain>(relaxed = true)
        save(p)
        assertEquals(listOf(p), getSaved().first())
    }

    @Test
    fun deletePlace_removes() = runTest {
        val repo = FakePlacesRepository()
        val save = SavePlaceUseCase(repo)
        val delete = DeletePlaceUseCase(repo)
        val getSaved = GetSavedPlacesUseCase(repo)

        val p1 = mockk<PlaceDomain>(relaxed = true)
        val p2 = mockk<PlaceDomain>(relaxed = true)
        save(p1); save(p2)

        delete(p1)
        assertEquals(listOf(p2), getSaved().first())
        assertFalse(getSaved().first().contains(p1))
    }

    @Test
    fun updatePlace_replaceOrAppend() = runTest {
        val repo = FakePlacesRepository()
        val save = SavePlaceUseCase(repo)
        val update = UpdatePlaceUseCase(repo)
        val getSaved = GetSavedPlacesUseCase(repo)

        val original = mockk<PlaceDomain>(relaxed = true)
        save(original)
        assertEquals(listOf(original), getSaved().first())

        update(original) // same instance -> replace
        assertEquals(listOf(original), getSaved().first())

        val updated = mockk<PlaceDomain>(relaxed = true) // different instance -> append in this fake
        update(updated)
        assertEquals(listOf(original, updated), getSaved().first())
    }

    @Test
    fun checkIfSaved_returnsBoolean() = runTest {
        val repo = FakePlacesRepository()
        val save = SavePlaceUseCase(repo)
        val check = CheckIfPlaceIsSavedUseCase(repo)

        val p = mockk<PlaceDomain>(relaxed = true)
        assertFalse(check(p))
        save(p)
        assertTrue(check(p))
    }

    @Test
    fun searchPlaces_returnsResults() = runTest {
        val repo = FakePlacesRepository()
        val search = SearchPlacesUseCase(repo)

        val q = "cafe"
        val p1 = mockk<PlaceDomain>(relaxed = true)
        val p2 = mockk<PlaceDomain>(relaxed = true)
        repo.seedSearch(q, listOf(p1, p2))

        val results = search(q)
        assertEquals(listOf(p1, p2), results)
    }

    @Test
    fun getPlaceById_returnsPlaceOrNull() = runTest {
        val repo = FakePlacesRepository()
        val getById = GetPlaceDetailsWithId(repo)

        val id = "place_123"
        val place = mockk<PlaceDomain>(relaxed = true)
        repo.seedDetails(id, place)

        assertEquals(place, getById(id))
        assertNull(getById("missing"))
    }
}
