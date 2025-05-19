package com.kastik.locationspoofer.ui.screens.main

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.maps.model.LatLng
import com.kastik.locationspoofer.LocationMockServiceState
import com.kastik.locationspoofer.UpdateLocationService
import com.kastik.locationspoofer.ui.screens.AvailableScreens
import com.kastik.locationspoofer.ui.screens.mapScreen.MapScreen
import com.kastik.locationspoofer.ui.screens.settingsScreen.SettingsScreen
import com.kastik.locationspoofer.ui.theme.LocationSpooferTheme

//todo when clicking mock without mock provier the locate me fab disappears
//todo dismiss popups after onClick

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("MissingPermission", "UnusedMaterial3ScaffoldPaddingParameter",
    "StateFlowValueCalledInComposition"
)
@Composable
@ExperimentalPermissionsApi
fun UIStuff(
    updateLocationService: UpdateLocationService,
    viewModel: MainUIViewModel = hiltViewModel()
) {
    val navController = rememberNavController()

    LocationSpooferTheme {
        NavHost(
            navController = navController,
            startDestination = AvailableScreens.MapScreen.name,
        ) {
            composable(AvailableScreens.MapScreen.name) {
                MapScreen(
                    navigate = { navController.navigate(it) },
                    updateLocationService = updateLocationService
                )
            }
            composable(AvailableScreens.Settings.name) {
                SettingsScreen()
            }
        }
    }
}