package com.kastik.locationspoofer.ui.screens.main

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.maps.routing.v2.RoutesGrpc
import com.kastik.locationspoofer.UserPreferences
import com.kastik.locationspoofer.service.UpdateLocationService
import com.kastik.locationspoofer.ui.screens.AvailableScreens
import com.kastik.locationspoofer.ui.screens.mapScreen.MapScreen
import com.kastik.locationspoofer.ui.screens.savedRoutesScreen.SavedRoutesScreen
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
    val userPreferences = viewModel.userPreferencesFlow.collectAsStateWithLifecycle(initialValue = UserPreferences.getDefaultInstance())

    LocationSpooferTheme(
        darkMode = userPreferences.value.darkMode
    ) {
        NavHost(
            navController = navController,
            startDestination = AvailableScreens.MapScreen .name,
        ) {
            composable(AvailableScreens.MapScreen.name) {
                MapScreen(
                    navigate = { navController.navigate(it) },
                    updateLocationService = updateLocationService,
                )
            }
            composable("${AvailableScreens.MapScreen.name}/{polyline}") { backStackEntry ->
                val polyline = backStackEntry.arguments?.getString("polyline")
                MapScreen(
                    navigate = { navController.navigate(it) },
                    updateLocationService = updateLocationService,
                    polyline = polyline
                )
            }
            composable(AvailableScreens.SettingsScreen.name) {
                SettingsScreen()
            }
            composable(AvailableScreens.SavedRoutesScreen.name) {
                SavedRoutesScreen(
                    onMockRoute = { polyline ->
                        navController.navigate("${AvailableScreens.MapScreen.name}/$polyline")
                    }
                )
            }
        }
    }
}