package com.kastik.locationspoofer.ui.screens.mapScreen.components.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable


@Composable
fun ErrorDialog(
    title: String,
    message: String?,
    onCLick: () -> Unit,
    dismiss: () -> Unit
) {

    AlertDialog(
        title = {
            Text(text = title)
        },
        text = {
            if (message != null) {
                Text(text = message)
            }
        },
        confirmButton = {
            TextButton(
                onClick = onCLick
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = dismiss
            ) {
                Text("Dismiss")
            }
        },
        onDismissRequest = dismiss,
    )
}