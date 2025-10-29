package com.kastik.locationspoofer.ui.screens.savedRoutesScreen.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.maps.places.v1.Place
import com.kastik.locationspoofer.SavedPlace
import com.kastik.locationspoofer.SavedRoute
import com.kastik.locationspoofer.domain.model.PlaceDomain
import com.kastik.locationspoofer.domain.model.RouteDomain
import com.kastik.locationspoofer.ui.theme.LocationSpooferTheme

@Composable
fun SavedRouteCard(
    modifier: Modifier = Modifier,
    route: RouteDomain,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onMockClick: () -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            if (!route.nickName.isNullOrEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        "Route",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(route.nickName, style = MaterialTheme.typography.titleMedium)
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        "From",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(route.origin.orEmpty(), style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "To",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(route.destination.orEmpty(), style = MaterialTheme.typography.titleMedium)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                //horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                    Spacer(Modifier.width(4.dp))
                    Text("Edit")
                }
                Spacer(Modifier.width(8.dp))
                OutlinedButton(
                    onClick = onDeleteClick, colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                    Spacer(Modifier.width(4.dp))
                    Text("Delete")
                }
                Spacer(Modifier.width(8.dp))
                TextButton(onClick = onMockClick) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Mock")
                    Text("Mock", maxLines = 1)
                }
            }
        }
    }
}


@Composable
fun SavedRouteCard(
    modifier: Modifier = Modifier,
    place: PlaceDomain,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onMockClick: () -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    "Place",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text( place.customName?.takeIf { it.isNotEmpty() } ?: place.name.orEmpty(), style = MaterialTheme.typography.titleMedium)
            }



            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                //horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                    Spacer(Modifier.width(4.dp))
                    Text("Edit")
                }
                Spacer(Modifier.width(8.dp))
                OutlinedButton(
                    onClick = onDeleteClick, colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                    Spacer(Modifier.width(4.dp))
                    Text("Delete")
                }
                Spacer(Modifier.width(8.dp))
                TextButton(onClick = onMockClick) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Mock")
                    Text("Mock", maxLines = 1)
                }
            }
        }
    }
}

/*
@Preview(
    name = "Light Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun SavedRouteCardPreview() {
    val routes = listOf<SavedRoute>(
        SavedRoute.newBuilder()
            .setOriginName("Origin")
            .setDestinationName("Destination")
            .setNickname("Some nickname")
            .build(),
        SavedRoute.newBuilder()
            .setOriginName("Origin")
            .setDestinationName("Destination")
            .setNickname("Some nickname")
            .build(),
        SavedRoute.newBuilder()
            .setOriginName("Origin")
            .setDestinationName("Destination")
            .build()
    )
    val places = listOf<SavedPlace>(
        SavedPlace.newBuilder().setPlace(
            Place.newBuilder()
                .setName("Some name")
                .build(),
        ).build(),
        SavedPlace.newBuilder().setPlace(
            Place.newBuilder()
                .setName("Some name")
                .build(),
        ).build(),
        SavedPlace.newBuilder().setPlace(
            Place.newBuilder()
                .setName("Some name")
                .build(),
        ).build()
    )

    LocationSpooferTheme {
        LazyColumn {
            items(routes) { route ->
                SavedRouteCard(
                    route = route,
                    onEditClick = {},
                    onMockClick = {},
                    onDeleteClick = {})
            }
            items(places) { place ->
                SavedRouteCard(
                    place = place,
                    onEditClick = {},
                    onMockClick = {},
                    onDeleteClick = {}
                )
            }
        }
    }
}

 */
