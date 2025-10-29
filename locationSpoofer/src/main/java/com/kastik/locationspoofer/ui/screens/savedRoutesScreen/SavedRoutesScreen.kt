package com.kastik.locationspoofer.ui.screens.savedRoutesScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kastik.locationspoofer.domain.model.PlaceDomain
import com.kastik.locationspoofer.domain.model.RouteDomain
import com.kastik.locationspoofer.ui.components.DialogState
import com.kastik.locationspoofer.ui.components.EditDialog
import com.kastik.locationspoofer.ui.screens.savedRoutesScreen.components.EmptySavedState
import com.kastik.locationspoofer.ui.screens.savedRoutesScreen.components.SavedRouteCard

@Composable
fun SavedRoutesScreen(
    modifier: Modifier = Modifier,
    viewModel: SavedRoutesScreenViewModel = hiltViewModel(),
    onMockRoute: (RouteDomain) -> Unit,
    onMockPlace: (PlaceDomain) -> Unit
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    SavedRoutesScreenContent(
        uiState = uiState,
        onMockRoute = onMockRoute,
        onMockPlace = onMockPlace,
        onEditRoute = viewModel::showEditDialog,
        onEditPlace = viewModel::showEditDialog,
        onDeleteRoute = viewModel::showDeleteDialog,
        onDeletePlace = viewModel::showDeleteDialog,
        onUpdateRoute = viewModel::updateRoute,
        onUpdatePlace = viewModel::updatePlace,
        onConfirmDeleteRoute = viewModel::deleteRoute,
        onConfirmDeletePlace = viewModel::deletePlace,
        onDismissDialogs = viewModel::dismissDialogs,
        modifier = modifier
    )
}

@Composable
fun SavedRoutesScreenContent(
    uiState: SavedRoutesScreenState,
    onMockRoute: (RouteDomain) -> Unit,
    onMockPlace: (PlaceDomain) -> Unit,
    onEditRoute: (RouteDomain) -> Unit,
    onEditPlace: (PlaceDomain) -> Unit,
    onDeleteRoute: (RouteDomain) -> Unit,
    onDeletePlace: (PlaceDomain) -> Unit,
    onUpdateRoute: (String, String, String, Double, Boolean) -> Unit,
    onUpdatePlace: (String) -> Unit,
    onConfirmDeleteRoute: (RouteDomain) -> Unit,
    onConfirmDeletePlace: (PlaceDomain) -> Unit,
    onDismissDialogs: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold {
        if (uiState.savedRoutes.isEmpty() && uiState.savedPlaces.isEmpty()) {
            EmptySavedState()
        } else {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(it),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.savedRoutes) { route ->
                    SavedRouteCard(
                        route = route,
                        onEditClick = { onEditRoute(route) },
                        onDeleteClick = { onDeleteRoute(route) },
                        onMockClick = { onMockRoute(route) }
                    )
                }
                items(uiState.savedPlaces) { place ->
                    SavedRouteCard(
                        place = place,
                        onEditClick = { onEditPlace(place) },
                        onDeleteClick = { onDeletePlace(place) },
                        onMockClick = { onMockPlace(place) }
                    )
                }
            }
        }

        AnimatedVisibility(
            uiState.dialogState is DialogState.EditRoute
        ) {
            val dialog =
                uiState.dialogState as? DialogState.EditRoute ?: return@AnimatedVisibility
            EditDialog(
                initialName = dialog.route.nickName.orEmpty(),
                initialOrigin = dialog.route.origin.orEmpty(),
                initialDestination = dialog.route.destination.orEmpty(),
                initialSpeed = dialog.route.speed,
                initialLoop = dialog.route.loop,
                onDismiss = onDismissDialogs,
                onSave = onUpdateRoute
            )
        }
        AnimatedVisibility(
            uiState.dialogState is DialogState.EditPlace
        ) {
            val dialog =
                uiState.dialogState as? DialogState.EditPlace ?: return@AnimatedVisibility
            EditDialog(
                nickname = dialog.place.customName ?: dialog.place.name.orEmpty(),
                onDismiss = onDismissDialogs,
                onSave = onUpdatePlace
            )
        }

        //TODO REPLACE WITH DELETE DIALOG
        AnimatedVisibility(
            uiState.dialogState is DialogState.DeleteRoute
        ) {
            val dialog =
                uiState.dialogState as? DialogState.DeleteRoute ?: return@AnimatedVisibility
            AlertDialog(
                onDismissRequest = onDismissDialogs,
                title = { Text("Delete Route") },
                text = { Text("Are you sure you want to delete this route?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onConfirmDeleteRoute(dialog.route)
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismissDialogs) {
                        Text("Cancel")
                    }
                }
            )
        }

        //TODO REPLACE WITH DELETE DIALOG

        AnimatedVisibility(
            uiState.dialogState is DialogState.DeletePlace
        ) {
            val dialog =
                uiState.dialogState as? DialogState.DeletePlace ?: return@AnimatedVisibility
            AlertDialog(
                onDismissRequest = onDismissDialogs,
                title = { Text("Delete Place") },
                text = { Text("Are you sure you want to delete this place?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onConfirmDeletePlace(dialog.place)
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismissDialogs) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
/*

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

    val mockPlaces = listOf(
        SavedPlace.newBuilder()
            .setPlace(
                Place.newBuilder()
                    .setName("Giannitsa")
                    .build(),
            ).build(),
        SavedPlace.newBuilder()
            .setPlace(
                Place.newBuilder()
                    .setName("Thessaloniki")
                    .build(),
            ).build()


    )
    LocationSpooferTheme {
        SavedRoutesScreenContent(
            savedRoutes = mockRoutes,
            savedPlaces = mockPlaces,
            onMockRoute = { },
            onDeleteRoute = { },
            onUpdateRoute = { _: SavedRoute, _: String, _: String, _: String, _: Boolean, _: Float -> },
            onMockPlace = {},
            onUpdatePlace = {_: SavedPlace, _: String -> },
            onDeletePlace = {},
        )
    }
}

 */