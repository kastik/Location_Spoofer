package com.kastik.locationspoofer.ui.screens.main

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.maps.routing.v2.Route
import com.kastik.locationspoofer.UserPreferences
import com.kastik.locationspoofer.ui.screens.MapRoute
import com.kastik.locationspoofer.ui.screens.MapRouteNavType
import com.kastik.locationspoofer.ui.screens.SavedRoutesRoute
import com.kastik.locationspoofer.ui.screens.SettingsRoute
import com.kastik.locationspoofer.ui.screens.mapScreen.MapScreen
import com.kastik.locationspoofer.ui.screens.savedRoutesScreen.SavedRoutesScreen
import com.kastik.locationspoofer.ui.screens.settingsScreen.SettingsScreen
import com.kastik.locationspoofer.ui.theme.LocationSpooferTheme
import kotlin.reflect.typeOf

//todo when clicking mock without mock provier the locate me fab disappears
//todo dismiss popups after onClick

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint(
    "MissingPermission", "UnusedMaterial3ScaffoldPaddingParameter",
    "StateFlowValueCalledInComposition"
)
@Composable
@ExperimentalPermissionsApi
fun UIStuff(
    viewModel: MainUIViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val userPreferences =
        viewModel.userPreferencesFlow.collectAsStateWithLifecycle(initialValue = UserPreferences.getDefaultInstance())

    LocationSpooferTheme(
        darkMode = userPreferences.value.darkMode
    ) {
        NavHost(
            navController = navController,
            startDestination = MapRoute(),
        ) {
            composable<MapRoute>(
                typeMap = mapOf(
                    typeOf<MapRoute>() to MapRouteNavType
                )
            ) { backStackEntry ->
                Log.d("MyLog","backstack")
                val route = backStackEntry.toRoute<MapRoute>().route
                Log.d("MyLog","after backstack")

                MapScreen(
                    route = route,
                    navigateToSettings = {
                        navController.navigate(SettingsRoute)
                    },
                    navigateToSavedRoutes = {
                        navController.navigate(SavedRoutesRoute)
                    }
                )
            }
            composable<SettingsRoute> {
                SettingsScreen()
            }

            composable<SavedRoutesRoute> {
                SavedRoutesScreen(
                    onMockRoute = { route ->
                        navController.navigate(
                            MapRoute(
                                route = route
                            )
                        )
                    },
                )
            }
        }
    }
}