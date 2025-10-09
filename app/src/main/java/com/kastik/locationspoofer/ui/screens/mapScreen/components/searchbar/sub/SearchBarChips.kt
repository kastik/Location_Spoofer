package com.kastik.locationspoofer.ui.screens.mapScreen.components.searchbar.sub

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kastik.locationspoofer.SavedPlaces
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.text.style.TextOverflow
import com.kastik.locationspoofer.SavedRoutes
import com.kastik.locationspoofer.data.models.MarkerData
import com.kastik.locationspoofer.data.models.toGmsLatLng
import com.kastik.locationspoofer.data.models.toMarkerData


@Composable
fun SearchBarChips(
    savedPlaces: SavedPlaces,
    savedRoutes: SavedRoutes,
    placeMarker: (MarkerData) -> Unit
){
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
    ) {
        items(savedRoutes.routesList) { route ->
            FilterChip(
                selected = true,
                label = {
                    Text(
                        text = route.originName.substringBefore('\n') + route.destinationName.substringBefore('\n'),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                onClick = {
                    route.visitDestinationsList.forEach { userDestination ->
                        placeMarker(userDestination.toGmsLatLng().toMarkerData())
                    }
                }
            )
        }
        items(savedPlaces.placeList) { place ->
            FilterChip(
                selected = true,
                label = {
                    Text(
                        text = place.name.substringBefore('\n'),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                onClick = {
                    placeMarker(place.toMarkerData())
                }
            )
        }
    }
}