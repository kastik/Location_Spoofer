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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.places.v1.Place
import com.kastik.locationspoofer.SavedPlaces
import com.kastik.locationspoofer.mapPlaceTypesToIcon
import com.kastik.locationspoofer.ui.screens.mapScreen.components.searchbar.sub.SearchBarChips

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopSearchBar(
    modifier: Modifier = Modifier,
    navigateToSettings: () -> Unit,
    searchResults: List<Place>,
    searchForPlace: (String) -> Unit,
    savedPlacesList: SavedPlaces,
    moveCameraToResultId: (String) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val queryMutableState = rememberSaveable { mutableStateOf("") }
    val isSearchBarExpandedMutableState = rememberSaveable { mutableStateOf(false) }

    Column {
        Box(
            modifier
                .fillMaxWidth()
                .semantics { isTraversalGroup = true }) {
            SearchBar(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .semantics { traversalIndex = 0f },
                inputField = {
                    SearchBarDefaults.InputField(
                        query = queryMutableState.value,
                        onQueryChange = { newQuery ->
                            queryMutableState.value = newQuery
                            searchForPlace(newQuery)
                        },
                        onSearch = { newQuery ->
                            searchForPlace(newQuery)
                            focusManager.clearFocus()
                        },
                        expanded = isSearchBarExpandedMutableState.value,
                        onExpandedChange = { isSearchBarExpandedMutableState.value = it },
                        placeholder = {
                            Text("Search for places")
                        },
                        leadingIcon = {
                            IconButton(onClick = navigateToSettings) {
                                Icon(Icons.Default.Settings, "Settings")
                            }
                        },
                        trailingIcon = {
                            if (isSearchBarExpandedMutableState.value) {
                                IconButton(onClick = {
                                    queryMutableState.value = ""
                                    focusManager.clearFocus()
                                    isSearchBarExpandedMutableState.value = false
                                }) {
                                    Icon(Icons.Default.Close, "Delete All Text")
                                }
                            }
                        })
                },
                expanded = isSearchBarExpandedMutableState.value,
                onExpandedChange = { isSearchBarExpandedMutableState.value = it },
            ) {
                LazyColumn {
                    items(searchResults) { result ->
                        ListItem(
                            headlineContent = { Text(result.name) },
                            leadingContent = {
                                Icon(
                                    mapPlaceTypesToIcon(result.primaryType),
                                    null
                                )
                            }, //TODO Clean map to icon
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                            modifier = Modifier
                                .clickable {
                                    moveCameraToResultId(result.id)
                                    focusManager.clearFocus()
                                    isSearchBarExpandedMutableState.value = false
                                    queryMutableState.value = ""
                                    isSearchBarExpandedMutableState.value = false
                                }
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp))
                    }
                }
            }
        }
        AnimatedVisibility(true) {
            SearchBarChips(
                savedPlaces = savedPlacesList,
                placeMarker = {
                    //viewModel.addMarker(it)
                })
        }
    }
}


@Preview(
    name = "Light Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun TopSearchBarPreview() {
    TopSearchBar(
        navigateToSettings = {},
        searchForPlace = {},
        searchResults = listOf(
            Place.newBuilder().setDisplayName(
                com.google.type.LocalizedText.newBuilder().setText("Central Park").build()
            ).build(),
        ),
        moveCameraToResultId = { _: String -> },
        savedPlacesList = SavedPlaces.newBuilder()
            .addPlace(Place.newBuilder().setName("njnjnjnjnjn").build()).build(),

        )
}