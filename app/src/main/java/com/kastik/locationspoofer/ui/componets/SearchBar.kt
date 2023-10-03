package com.kastik.locationspoofer.ui.componets

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceTypes
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.kastik.locationspoofer.data.MyViewModel
import com.kastik.locationspoofer.ui.screens.AvailableScreens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopSearchBar(viewModel: MyViewModel, navController: NavController, placesClient: PlacesClient) {
    val searchActive = remember { mutableStateOf(false) }
    val searchText = remember { mutableStateOf("") }
    val pubResponseNames = remember { mutableStateListOf<String>() }
    val pubResponseIds = remember { mutableStateListOf<String>() }
    val token = AutocompleteSessionToken.newInstance()
    val focusManager = LocalFocusManager.current


    Column(
        Modifier
            .fillMaxWidth()
            .padding(0.dp, 20.dp, 0.dp, 0.dp)) {
        SearchBar(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            leadingIcon = {

                if (searchActive.value) {
                    IconButton(onClick = {
                        searchActive.value = false
                        searchText.value = ""
                    }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                } else {
                    IconButton(onClick = {
                        navController.navigate(AvailableScreens.Settings.name)
                    }) {
                        Icon(Icons.Default.Settings, "Settings")
                    }
                }
            },
            trailingIcon = {
                if (searchActive.value) {
                    IconButton(onClick = {
                        searchText.value = ""
                    }) {
                        Icon(Icons.Default.Close, "Delete All Text")
                    }
                } else {
                    IconButton(
                        onClick = {
                            if (viewModel.searchBarMyLocationEnabled().value) {
                                viewModel.getCameraState().value.move(
                                    CameraUpdateFactory.newLatLng(
                                        viewModel.getMapPosition().value!!
                                    )
                                )
                            } else {
                                viewModel.showLocationErrorDialog(true)
                            }

                        }) {
                        Icon(Icons.Default.LocationOn, "Locate")
                    }
                }
            },
            active = searchActive.value,
            query = searchText.value,
            onActiveChange = { searchActive.value = it },
            onQueryChange = {
                searchText.value = it
                pubResponseIds.removeAll(pubResponseIds)
                pubResponseNames.removeAll(pubResponseNames)
                val request =
                    FindAutocompletePredictionsRequest.builder()
                        .setTypesFilter(listOf(PlaceTypes.REGIONS))
                        .setSessionToken(token)
                        .setQuery(searchText.value)
                        .build()
                placesClient.findAutocompletePredictions(request)
                    .addOnSuccessListener { response: FindAutocompletePredictionsResponse ->
                        for (prediction in response.autocompletePredictions) {
                            pubResponseNames.add(
                                prediction.getPrimaryText(null).toString()
                            )
                            pubResponseIds.add(prediction.placeId)
                        }
                    }
            },
            onSearch = { focusManager.clearFocus() },
            placeholder = { Text("Search Something") }) {
            LazyColumn {
                itemsIndexed(pubResponseNames) { index, item ->
                    Card(Modifier.fillMaxWidth()) {
                        Text(item, modifier = Modifier.clickable {
                            searchText.value = ""
                            Log.d(
                                "MyLog",
                                "Clicked: $item with id ${pubResponseIds[index]}"
                            )

                            val placeFields = listOf(Place.Field.LAT_LNG)
                            val request = FetchPlaceRequest.newInstance(
                                pubResponseIds[index],
                                placeFields
                            )


                            placesClient.fetchPlace(request)
                                .addOnSuccessListener { response: FetchPlaceResponse ->
                                    val place = response.place
                                    viewModel.setMarker(place.latLng)
                                    searchActive.value = false
                                    Log.i("MyLog", "Place found: ${place.name}")
                                }
                        })
                    }
                }
            }
            Text("Powered By Google") //TODO
            //Also TODO ADD ONFAILURE LISTENER FOR OFFLINE
        }
    }
}