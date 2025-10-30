package com.kastik.androidtest

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kastik.locationspoofer.data.datasource.local.SpoofDataSourceImpl
import com.kastik.locationspoofer.domain.model.RouteDomain
import com.kastik.locationspoofer.ui.screens.mapScreen.SpoofState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class SpoofDataSourceImplInstrumentedTest {

    private lateinit var context: Context
    private lateinit var dataSource: SpoofDataSourceImpl

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        dataSource = SpoofDataSourceImpl(context)
    }

    @After
    fun tearDown() {
        dataSource.cleanup()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun bindsToService_andUpdatesState() = runTest {
        val route = RouteDomain(encodedPolyline = "encoded_polyline_string")
        dataSource.startSpoofing(route, loopOnFinish = false, resetOnFinish = false)

        val state = dataSource.spoofState.first { it is SpoofState.Spoofing }
        assertTrue(state is SpoofState.Spoofing)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun unbindsService_andResetsState() = runTest {
        dataSource.cleanup()
        assertEquals(SpoofState.Idle, dataSource.spoofState.value)
    }
}