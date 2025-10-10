package com.kastik.locationspoofer.ui.screens.mapScreen.components.searchbar

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.places.v1.Place
import com.kastik.locationspoofer.SavedPlaces
import com.kastik.locationspoofer.SavedRoutes
import com.kastik.locationspoofer.mapPlaceTypesToIcon
import com.kastik.locationspoofer.toLatLngBounds
import com.kastik.locationspoofer.ui.screens.mapScreen.components.searchbar.sub.SearchBarChips

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopSearchBar(
    navigateToSettings: () -> Unit,
    searchPlaces: (String) -> Unit,
    moveToPlaceWithId: (String, LatLngBounds) -> Unit,
    placeResults: List<Place>,
    modifier: Modifier = Modifier,
    savedPlacesState: SavedPlaces,
    savedRoutesState: SavedRoutes,
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val query = rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }

    Column {
        Box(
            modifier
                .fillMaxWidth()
                .semantics { isTraversalGroup = true }
        ) {
            SearchBar(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .semantics { traversalIndex = 0f },
                inputField = {
                    SearchBarDefaults.InputField(
                        query = query.value,
                        onQueryChange = { newQuery ->
                            query.value = newQuery
                            searchPlaces(newQuery)
                        },
                        onSearch = { newQuery ->
                            searchPlaces(newQuery)
                            focusManager.clearFocus()
                        },
                        expanded = expanded,
                        onExpandedChange = { expanded = it },
                        placeholder = {
                            Text("Search for places")
                        },
                        leadingIcon = {
                            IconButton(onClick = {
                                navigateToSettings()
                            }) {
                                Icon(Icons.Default.Settings, "Settings")
                            }
                        },
                        trailingIcon = {
                            if (expanded) {
                                IconButton(onClick = {
                                    query.value = ""
                                    focusManager.clearFocus()
                                    expanded = false
                                }) {
                                    Icon(Icons.Default.Close, "Delete All Text")
                                }
                            }
                        }
                    )
                },
                expanded = expanded,
                onExpandedChange = { expanded = it },
            ) {
                LazyColumn {
                    items(placeResults) { place ->
                        ListItem(
                            headlineContent = { Text(place.name) },
                            leadingContent = { Icon(mapPlaceTypesToIcon(place.typesList), null) },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                            modifier = Modifier
                                .clickable {
                                    moveToPlaceWithId(place.id,place.viewport.toLatLngBounds())
                                    focusManager.clearFocus()
                                    expanded = false
                                    query.value = ""
                                    expanded = false
                                }
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
        AnimatedVisibility(true) {
            SearchBarChips(
                savedPlaces = savedPlacesState,
                savedRoutes = savedRoutesState,
                placeMarker = {
                    //viewModel.addMarker(it)
                })
        }
    }
}



@Preview(
    name = "Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun TopSearchBarPreview() {
    TopSearchBar(
        navigateToSettings = {},
        searchPlaces = {},
        placeResults = listOf(
            Place.newBuilder()
                .setDisplayName(com.google.type.LocalizedText.newBuilder().setText("Central Park").build())
                .build(),
        ),
        moveToPlaceWithId = {_:String, _: LatLngBounds -> },
        savedPlacesState = SavedPlaces.newBuilder().addPlace(Place.newBuilder().setName("njnjnjnjnjn").build()).build(),
        savedRoutesState = SavedRoutes.getDefaultInstance(),

    )
}