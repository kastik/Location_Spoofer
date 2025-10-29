package com.kastik.locationspoofer.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.kastik.locationspoofer.domain.model.PlaceDomain
import com.kastik.locationspoofer.domain.model.RouteDomain


@Composable
fun DeleteDialog(
    route: RouteDomain,
    onConfirm: (RouteDomain) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Route") },
        text = { Text("Are you sure you want to delete this route?") },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(route) },
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun DeleteDialog(
    place: PlaceDomain,
    onConfirm: (PlaceDomain) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Place") },
        text = { Text("Are you sure you want to delete this place?") },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(place) },
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}