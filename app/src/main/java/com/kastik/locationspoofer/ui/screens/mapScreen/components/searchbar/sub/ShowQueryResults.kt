package com.kastik.locationspoofer.ui.screens.mapScreen.components.searchbar.sub

import android.app.Activity
import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.maps.places.v1.Place
import com.kastik.locationspoofer.data.models.toGmsLatLng
import com.kastik.locationspoofer.data.models.toMarkerData
import java.util.concurrent.Executor

@Composable
fun ShowQueryResults(
    placesResults: List<Place>,
    setMarker: (LatLng) -> Unit
) {
    LazyColumn {
        items(placesResults) { place ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
            ) {
                Text(
                    text = place.displayName.text,
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                    modifier = Modifier
                        .clickable {
                            Log.d("MyLog", "Clicked on ${place.displayName.text}")
                            Log.d("MyLog","Location is ${place.location.longitude}")
                            setMarker(place.location.toGmsLatLng())
                        }
                )
            }
        }
    }
    Text(
        "Powered by Google",
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.padding(8.dp)
    )
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
fun ShowQueryResultsPreview() {
    val mockPlaces = listOf(
        Place.newBuilder()
            .setDisplayName(com.google.type.LocalizedText.newBuilder().setText("Central Park").build())
            .build(),
        Place.newBuilder()
            .setDisplayName(com.google.type.LocalizedText.newBuilder().setText("Times Square").build())
            .build()
    )
    ShowQueryResults(
        placesResults = mockPlaces,
        setMarker = { }
    )
}