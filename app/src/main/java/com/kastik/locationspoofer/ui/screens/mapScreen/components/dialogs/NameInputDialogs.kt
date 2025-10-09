package com.kastik.locationspoofer.ui.screens.mapScreen.components.dialogs

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.kastik.locationspoofer.ui.theme.LocationSpooferTheme

@Composable
fun NameInputDialog(
    title: String,
    firstLabel: String,
    firstValue: String,
    onFirstValueChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    secondLabel: String? = null,
    secondValue: String? = null,
    onSecondValueChange: ((String) -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = firstValue,
                    onValueChange = onFirstValueChange,
                    label = { Text(firstLabel) }
                )
                if (secondLabel != null && secondValue != null && onSecondValueChange != null) {
                    OutlinedTextField(
                        value = secondValue,
                        onValueChange = onSecondValueChange,
                        label = { Text(secondLabel) }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
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
fun NameInputDialogPreview() {
    LocationSpooferTheme {
        NameInputDialog(
            title = "Enter route names",
            firstLabel = "Origin name",
            firstValue = "Origin",
            onFirstValueChange = {},
            onDismiss = {},
            onConfirm = {},
            secondLabel = "Destination name",
            secondValue = "Destination",
            onSecondValueChange = {}
        )
    }
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
fun NameInputDialogPointPreview() {
    LocationSpooferTheme {
        NameInputDialog(
            title = "Enter a name",
            firstLabel = "Point name",
            firstValue = "Some Point",
            onFirstValueChange = {},
            onDismiss = {},
            onConfirm = {}
        )
    }
}
