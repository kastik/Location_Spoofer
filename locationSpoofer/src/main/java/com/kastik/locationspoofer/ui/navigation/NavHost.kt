package com.kastik.locationspoofer.ui.navigation

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.kastik.locationspoofer.SavedPlace
import com.kastik.locationspoofer.SavedRoute
import com.kastik.locationspoofer.UserPreferences
import com.kastik.locationspoofer.data.mapers.toPlaceDomain
import com.kastik.locationspoofer.data.mapers.toRouteDomain
import com.kastik.locationspoofer.data.mapers.toRouteProto
import com.kastik.locationspoofer.data.mapers.toSavedPlaceProto
import com.kastik.locationspoofer.domain.model.PlaceDomain
import com.kastik.locationspoofer.domain.model.RouteDomain
import com.kastik.locationspoofer.ui.screens.mapScreen.MapScreen
import com.kastik.locationspoofer.ui.screens.savedRoutesScreen.SavedRoutesScreen
import com.kastik.locationspoofer.ui.screens.settingsScreen.SettingsScreen
import com.kastik.locationspoofer.ui.theme.LocationSpooferTheme
import kotlin.io.encoding.Base64

//todo when clicking mock without mock provier the locate me fab disappears
//todo dismiss popups after onClick

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint(
    "MissingPermission", "UnusedMaterial3ScaffoldPaddingParameter",
    "StateFlowValueCalledInComposition"
)
@Composable
@ExperimentalPermissionsApi
fun NavHost(
    viewModel: NavHostViewModel = hiltViewModel()
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
                enterTransition = { scaleIn() },
                exitTransition = { fadeOut() },
                //popExitTransition = { expandIn() },
                popEnterTransition = { fadeIn() }
            ){ backStackEntry ->
                val mapRoute = backStackEntry.toRoute<MapRoute>()
                MapScreen(
                    navigateToSettings = {
                        navController.navigate(
                            route = SettingsRoute,
                            navOptions = NavOptions.Builder().setLaunchSingleTop(true).setRestoreState(true).build()
                        )
                    },
                    navigateToSavedRoutes = {
                        navController.navigate(
                            route = SavedRoutesRoute,
                            navOptions = NavOptions.Builder().setLaunchSingleTop(true).setRestoreState(true).build()
                        )
                    }
                )
            }
            composable<SettingsRoute>(
                enterTransition = { slideInVertically(
                    initialOffsetY = { it },
                ) },
                exitTransition = { slideOutVertically(
                    targetOffsetY = { it },
                ) }
            ) {
                SettingsScreen()
            }

            composable<SavedRoutesRoute> (
                enterTransition = { slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = spring()
                ) },
                exitTransition = { slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = spring()
                ) }
            ){
                SavedRoutesScreen(
                    navigateToMap = {
                        navController.navigate(
                            MapRoute(),
                            navOptions = NavOptions.Builder()
                                .setLaunchSingleTop(true)
                                .setRestoreState(true)
                                .setPopUpTo(MapRoute(), inclusive = false)
                                .build())
                    },
                )
            }
        }
    }
}


//TODO REMOVE THESE
fun String.decodeToRoute(): SavedRoute? {
    val bytes = Base64.decode(this)
    return SavedRoute.parseFrom(bytes)
}

fun RouteDomain.encodeToString(): String {
    val abc = this.toRouteProto()
    val abc2 = abc.toByteArray()
    return Base64.encode(abc2)
}

fun PlaceDomain.encodeToString(): String {
    val abc = this.toSavedPlaceProto()
    val abc2 = abc.toByteArray()
    return Base64.encode(abc2)
}

fun String.decodeToPlace(): SavedPlace? {
    val bytes = Base64.decode(this)
    return SavedPlace.parseFrom(bytes)
}