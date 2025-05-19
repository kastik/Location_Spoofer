package com.kastik.locationspoofer.ui.screens.mapScreen.components.searchbar.sub

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.kastik.locationspoofer.data.models.PlaceResult
import androidx.compose.foundation.lazy.items
import com.google.android.gms.maps.model.LatLng
import com.kastik.locationspoofer.data.models.toLatLng

@Composable
fun ShowQueryResults(
    placesResults: List<PlaceResult>,
    fetchPlace: (FetchPlaceRequest) -> Task<FetchPlaceResponse>,
    setMarker: (LatLng) -> Unit
) {
    LazyColumn {
        items(placesResults) { item ->
            Column(
                Modifier.Companion
                    .fillMaxWidth()
                    .clickable {
                        item.toLatLng(fetchPlace) { location ->
                            setMarker(location)
                            println(location)
                        }
                        //setMarker(item.placeId)
                    }
            ) {
                Text(
                    text = item.placeName,
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                    modifier = Modifier.Companion.padding(5.dp)
                )
            }
        }
    }
    Text("Powered By Google") //TODO Check Docs for watermark
    //Also TODO ADD ONFAILURE LISTENER FOR OFFLINE

}