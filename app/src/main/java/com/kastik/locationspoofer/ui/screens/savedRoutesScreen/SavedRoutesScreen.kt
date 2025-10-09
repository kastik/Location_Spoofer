package com.kastik.locationspoofer.ui.screens.savedRoutesScreen

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.maps.routing.v2.Route
import com.kastik.locationspoofer.SavedRoute
import com.kastik.locationspoofer.SavedRoutes
import com.kastik.locationspoofer.ui.screens.mapScreen.components.fab.sub.LocationSpoofButton
import com.kastik.locationspoofer.ui.screens.savedRoutesScreen.components.EditSavedRouteDialog
import com.kastik.locationspoofer.ui.screens.savedRoutesScreen.components.SavedRouteCard
import com.kastik.locationspoofer.ui.theme.LocationSpooferTheme

@Composable
fun SavedRoutesScreen(
    modifier: Modifier = Modifier,
    viewModel: SavedRoutesScreenViewModel = hiltViewModel(),
    onMockRoute: (String) -> Unit
) {
    val savedRoutes by viewModel.savedRoutes.collectAsState()
    SavedRoutesScreenContent(
        savedRoutes = savedRoutes.routesList,
        onMockRoute = onMockRoute,
        modifier = modifier,
        onUpdateRoute = { route, newOrigin, newDestination, newName, newLoop, newSpeed ->
            viewModel.updateRoute(route, newOrigin, newDestination, newName, newLoop, newSpeed)
        },
        onDeleteRoute = { route ->
            viewModel.deleteRoute(route)
        }
    )
}

@Composable
fun SavedRoutesScreenContent(
    savedRoutes: List<SavedRoute>,
    onMockRoute: (String) -> Unit,
    onUpdateRoute: (SavedRoute, String, String, String, Boolean, Float) -> Unit,
    onDeleteRoute: (SavedRoute) -> Unit,
    modifier: Modifier = Modifier,

    ) {
    var editingRoute by remember { mutableStateOf<SavedRoute?>(null) }
    var routeToDelete by remember { mutableStateOf<SavedRoute?>(null) }

    Scaffold {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(it),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(savedRoutes) { route ->
                SavedRouteCard(
                    startLocationName = route.originName,
                    endLocationName = route.destinationName,
                    routeNickName = route.nickname,
                    onEditClick = { editingRoute = route },
                    onDeleteClick = { routeToDelete = route },
                    onMockClick = {
                        val encodedPolyline = route.route.polyline
                        if (encodedPolyline.hasEncodedPolyline()) {
                            onMockRoute(encodedPolyline.toString())
                        }
                    }
                )
            }
        }
    }


    editingRoute?.let { route ->
        EditSavedRouteDialog(
            initialName = route.nickname,
            initialOrigin = route.originName,
            initialDestination = route.destinationName,
            initialSpeed = route.speed,
            initialLoop = route.loop,
            onDismiss = { editingRoute = null },
            onSave = { name, origin, destination, speed, loop ->
                onUpdateRoute(
                    route,
                    origin,
                    destination,
                    name,
                    loop,
                    speed
                )
                editingRoute = null
            }
        )
    }

    if (routeToDelete != null) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { routeToDelete = null },
            title = { Text("Delete Route") },
            text = { Text("Are you sure you want to delete this route?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteRoute(routeToDelete!!)
                        routeToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { routeToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}


@Preview(
    name = "Light Mode",
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    name = "Dark Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun SavedRoutesScreenPreview() {
    val mockRoutes = listOf(
        SavedRoute.newBuilder()
            .setOriginName("Giannitsa")
            .setDestinationName("Thessaloniki")
            .build(),
        SavedRoute.newBuilder()
            .setOriginName("Lambda Center")
            .setDestinationName("Neos Sidirodromikos Stathmos")
            .build(),
        SavedRoute.newBuilder()
            .setOriginName("Kallikrateia")
            .setDestinationName("Giannitsa")
            .build(),
        SavedRoute.newBuilder()
            .setOriginName("Platia Amerikis")
            .setDestinationName("Omonia")
            .build()
    )
    LocationSpooferTheme {
        SavedRoutesScreenContent(
            savedRoutes = mockRoutes,
            onMockRoute = { },
            onDeleteRoute = { },
            onUpdateRoute = {_: SavedRoute, _: String, _: String, _: String, _: Boolean, _: Float ->}
        )
    }
}