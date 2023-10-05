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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceTypes
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.kastik.locationspoofer.data.MyViewModel
import com.kastik.locationspoofer.ui.screens.AvailableScreens
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopSearchBar(
    viewModel: MyViewModel,
    navController: NavController,
    placesClient: PlacesClient) {

    val focusManager = LocalFocusManager.current
    val queryText = remember { mutableStateOf("") }

    Column(
        Modifier

            .fillMaxWidth()
            .padding(0.dp, 20.dp, 0.dp, 0.dp)
    ) {
        SearchBar(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            leadingIcon = {LeadingIconButton(viewModel,queryText,navController)},
            trailingIcon = { TrailingIconButton(viewModel,queryText) },
            active = viewModel.searchActive().value,
            query = viewModel.searchText().value,
            onActiveChange = { viewModel.searchActive().value = it },
            onQueryChange = {newValue -> query(viewModel,newValue,placesClient) },
            onSearch = { focusManager.clearFocus() },
            placeholder = { Text("Search Something") },
            content = {ShowQueryResults(viewModel,placesClient)}
        )

    }
}

@Composable
private fun TrailingIconButton(viewModel: MyViewModel,queryText: MutableState<String>){
    val scope = rememberCoroutineScope()
    if (viewModel.searchActive().value) {
        IconButton(onClick = {
            queryText.value = ""
        }) {
            Icon(Icons.Default.Close, "Delete All Text")
        }
    } else {
        IconButton(
            onClick = {
                if (viewModel.searchBarMyLocationEnabled().value) {
                    scope.launch {
                        viewModel.cameraState.animate(
                            CameraUpdateFactory.newLatLng(
                                viewModel.getUserPosition().value!!
                            )
                        )
                        viewModel.cameraState.animate(
                            CameraUpdateFactory.zoomTo(13f)
                        )
                        Log.d("MyLog", "Animate!null")
                    }}else{
                    viewModel.showLocationErrorDialog(true)
                    Log.d("MyLog", "Animate==null")
                }


            }) {
            Icon(Icons.Default.LocationOn, "Locate")
        }
    }
}

@Composable
private fun LeadingIconButton(viewModel: MyViewModel,queryText: MutableState<String>,navController: NavController){
        if (viewModel.searchActive().value) {
            IconButton(onClick = {
                viewModel.searchActive().value = false
                queryText.value = ""
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

}

private fun query(viewModel: MyViewModel, newValue: String, placesClient: PlacesClient) {

        viewModel.pubResponseIds().removeAll(viewModel.pubResponseIds())
        viewModel.pubResponseNames().removeAll(viewModel.pubResponseNames())
        val request =
            FindAutocompletePredictionsRequest.builder()
                .setTypesFilter(listOf(PlaceTypes.REGIONS))
                .setSessionToken(viewModel.placesSessionToken)
                .setQuery(newValue)
                .build()
        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response: FindAutocompletePredictionsResponse ->
                for (prediction in response.autocompletePredictions) {
                    viewModel.pubResponseNames().add(
                        prediction.getPrimaryText(null).toString()
                    )
                    viewModel.pubResponseIds().add(prediction.placeId)
                }
            }


}


@Composable
private fun ShowQueryResults(viewModel: MyViewModel,placesClient: PlacesClient) {
        LazyColumn {
            itemsIndexed(viewModel.pubResponseNames()) { index, item ->
                Card(Modifier.fillMaxWidth()) {
                    Text(item, modifier = Modifier.clickable {
                        viewModel.searchText().value = ""
                        Log.d(
                            "MyLog",
                            "Clicked: $item with id ${viewModel.pubResponseIds()[index]}"
                        )

                        val placeFields = listOf(Place.Field.LAT_LNG)
                        val request = FetchPlaceRequest.newInstance(
                            viewModel.pubResponseIds()[index],
                            placeFields
                        )


                        placesClient.fetchPlace(request)
                            .addOnSuccessListener { response: FetchPlaceResponse ->
                                val place = response.place
                                viewModel.setMarker(place.latLng)
                                viewModel.searchActive().value = false
                                Log.i("MyLog", "Place found: ${place.name}")
                            }
                    })
                }
            }
        }
        Text("Powered By Google") //TODO Check Docs for watermark
        //Also TODO ADD ONFAILURE LISTENER FOR OFFLINE

}