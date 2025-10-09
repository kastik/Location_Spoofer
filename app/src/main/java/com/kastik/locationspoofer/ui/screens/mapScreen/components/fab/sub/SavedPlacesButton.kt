package com.kastik.locationspoofer.ui.screens.mapScreen.components.fab.sub

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SavedPlacesButton(
    navigateToSavedRoutesScreen: () -> Unit
) {
    FloatingActionButton(
        modifier = Modifier.Companion.padding(top = 6.dp), onClick = navigateToSavedRoutesScreen
    ) {
        Icon(Icons.Default.SaveAlt, contentDescription = "Spoof Control")
    }
}
