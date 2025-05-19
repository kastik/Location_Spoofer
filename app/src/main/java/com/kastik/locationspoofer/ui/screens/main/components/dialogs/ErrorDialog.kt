package com.kastik.locationspoofer.ui.screens.main.components.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.kastik.locationspoofer.ui.screens.main.components.map.MapScreenState

@Composable
fun ErrorDialog(
    mapScreenState: MapScreenState,
    dismiss: () -> Unit
) {
    AnimatedVisibility(
        visible = mapScreenState is MapScreenState.Error,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        if (mapScreenState is MapScreenState.Error) {
            AlertDialog(
                title = {
                    Text(text = mapScreenState.displayTitle)
                },
                text = {
                    Text(text = mapScreenState.displayMsg)
                },
                onDismissRequest = dismiss,
                confirmButton = {
                    TextButton(
                        onClick = mapScreenState.action
                    ) {
                        Text("Confirm")
                    }
                },
            )
        }
    }
}