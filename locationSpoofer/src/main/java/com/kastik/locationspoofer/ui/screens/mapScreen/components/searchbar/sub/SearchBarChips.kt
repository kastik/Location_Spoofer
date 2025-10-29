package com.kastik.locationspoofer.ui.screens.mapScreen.components.searchbar.sub

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kastik.locationspoofer.domain.model.LatLngDomain
import com.kastik.locationspoofer.domain.model.PlaceDomain


@Composable
fun SearchBarChips(
    savedPlaces: List<PlaceDomain>,
    placeMarker: (LatLngDomain) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
    ) {
        items(savedPlaces) { place ->
            FilterChip(
                selected = true,
                label = {
                    Text(
                        text = place.customName
                            ?.substringBefore('\n')
                            ?.takeIf { it.isNotEmpty() }
                            ?: place.name
                            ?: "",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                onClick = {
                    placeMarker(
                        place.location
                    )
                }
            )
        }
    }
}