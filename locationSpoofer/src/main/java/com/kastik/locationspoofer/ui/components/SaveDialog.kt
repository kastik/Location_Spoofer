package com.kastik.locationspoofer.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun SaveDialog(
    title: String,
    firstLabel: String,
    initialFirstValue: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    NameInputDialogInternal(
        title = title,
        firstLabel = firstLabel,
        initialFirstValue = initialFirstValue,
        secondLabel = null,
        initialSecondValue = null,
        onConfirm = { first, _ -> onConfirm(first) },
        onDismiss = onDismiss
    )
}

@Composable
fun SaveDialog(
    title: String,
    firstLabel: String,
    initialFirstValue: String,
    secondLabel: String,
    initialSecondValue: String,
    onConfirm: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    NameInputDialogInternal(
        title = title,
        firstLabel = firstLabel,
        initialFirstValue = initialFirstValue,
        secondLabel = secondLabel,
        initialSecondValue = initialSecondValue,
        onConfirm = { first, second -> onConfirm(first, second.orEmpty()) },
        onDismiss = onDismiss
    )
}

@Composable
private fun NameInputDialogInternal(
    title: String,
    firstLabel: String,
    initialFirstValue: String,
    secondLabel: String?,
    initialSecondValue: String?,
    onConfirm: (String, String?) -> Unit,
    onDismiss: () -> Unit
) {
    var firstTextValue by remember { mutableStateOf(initialFirstValue) }
    var secondTextValue by remember { mutableStateOf(initialSecondValue.orEmpty()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = firstTextValue,
                    onValueChange = { firstTextValue = it },
                    label = { Text(firstLabel) }
                )
                if (secondLabel != null) {
                    OutlinedTextField(
                        value = secondTextValue,
                        onValueChange = { secondTextValue = it },
                        label = { Text(secondLabel) }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(firstTextValue, secondTextValue.takeIf { secondLabel != null }) }) {
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

/*
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
            onDismiss = {},
            onConfirm = {},
            secondLabel = "Destination name",
            secondValue = "Destination",
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
            onDismiss = {},
            onConfirm = {}
        )
    }
}
 */