package com.kastik.locationspoofer.ui.screens.mapScreen.components.searchbar

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.PlaceTypes
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.kastik.locationspoofer.data.models.PlaceResult
import com.kastik.locationspoofer.ui.screens.mapScreen.components.searchbar.sub.ShowQueryResults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopSearchBar(
    visible: Boolean,
    navigateToSettings: () -> Unit,
    moveCamera: (LatLng) -> Unit = {},
    savedPlacesView: @Composable()  () -> Unit,
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val query = remember { mutableStateOf("") }
    val responses: MutableList<PlaceResult> = remember { mutableStateListOf() }
    val placesClient = remember { Places.createClient(context) }
    val expandedSearchBarState: MutableState<Boolean> = remember { mutableStateOf(false) }

    val placesResults = { query: String ->
        val request =
            FindAutocompletePredictionsRequest.builder()
                .setTypesFilter(listOf(PlaceTypes.REGIONS))
                .setQuery(query)
                .build()
        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response: FindAutocompletePredictionsResponse ->
                val results = response.autocompletePredictions.map {
                    Log.d("MyLog", it.toString())
                    PlaceResult(
                        placeName = it.getPrimaryText(null).toString(),
                        placeId = it.placeId
                    )
                }
                responses.clear()
                responses.addAll(results)
            }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            //.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {

        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(),
            exit = slideOutVertically()
        ) {

            Column(
                Modifier.Companion
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.Companion.CenterHorizontally
            ) {
                DockedSearchBar(
                    inputField = {
                        SearchBarDefaults.InputField(
                            query = query.value,
                            //onQueryChange = { onQueryChange(it) },
                            onQueryChange = { newQuery ->
                                query.value = newQuery
                                placesResults(newQuery)
                            },
                            onSearch = {
                                focusManager.clearFocus()
                            },
                            expanded = expandedSearchBarState.value,
                            onExpandedChange = { expandedSearchBarState.value = it },
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
                                if (expandedSearchBarState.value) {
                                    IconButton(onClick = {
                                        query.value = ""
                                        focusManager.clearFocus()
                                        expandedSearchBarState.value = false
                                    }) {
                                        Icon(Icons.Default.Close, "Delete All Text")
                                    }
                                }
                            },
                        )
                    },
                    expanded = expandedSearchBarState.value,
                    onExpandedChange = { expandedChange ->
                        expandedSearchBarState.value = expandedChange
                    },
                ) {
                    ShowQueryResults(
                        placesResults = responses,
                        setMarker = {
                            moveCamera(it)
                            focusManager.clearFocus()
                            expandedSearchBarState.value = false
                            query.value = ""
                        },
                        fetchPlace = { placesClient.fetchPlace(it) }
                    )

                }
                savedPlacesView()
            }
        }
    }
}