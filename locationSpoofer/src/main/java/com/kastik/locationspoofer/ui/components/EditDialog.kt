package com.kastik.locationspoofer.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun EditDialog(
    initialName: String,
    initialOrigin: String,
    initialDestination: String,
    initialSpeed: Double,
    initialLoop: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, String, String, Double, Boolean) -> Unit
) {
    var name by remember { mutableStateOf(TextFieldValue(initialName)) }
    var origin by remember { mutableStateOf(TextFieldValue(initialOrigin)) }
    var destination by remember { mutableStateOf(TextFieldValue(initialDestination)) }
    var speed by remember { mutableStateOf(initialSpeed) }
    var loop by remember { mutableStateOf(initialLoop) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Edit Saved Route",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Custom name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = origin,
                    onValueChange = { origin = it },
                    label = { Text("Origin name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = destination,
                    onValueChange = { destination = it },
                    label = { Text("Destination name") },
                    modifier = Modifier.fillMaxWidth()
                )
                if(false) {
                    //TODO Enable this once the speed is calculated properly
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Speed: %.1f".format(speed))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Button(
                                onClick = { if (speed > 0.1f) speed -= 0.1f },
                                contentPadding = PaddingValues(horizontal = 12.dp)
                            ) {
                                Text("-")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { speed += 0.1f },
                                contentPadding = PaddingValues(horizontal = 12.dp)
                            ) {
                                Text("+")
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Loop Route")
                    Switch(
                        checked = loop,
                        onCheckedChange = { loop = it }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Button(onClick = {
                        onSave(
                            name.text,
                            origin.text,
                            destination.text,
                            speed,
                            loop
                        )
                    }) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@Composable
fun EditDialog(
    nickname: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var nickname by remember { mutableStateOf(TextFieldValue(nickname)) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Edit Place",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                OutlinedTextField(
                    value = nickname,
                    onValueChange = { nickname = it },
                    label = { Text("Custom name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Button(onClick = {
                        onSave(
                            nickname.text,
                        )
                    }) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true, device = "id:pixel_8")
@Composable
fun EditSavedRoutePreview() {
    EditDialog(
        initialName = "Morning Route",
        initialOrigin = "Home",
        initialDestination = "Work",
        initialSpeed = 1.2,
        initialLoop = false,
        onDismiss = {},
        onSave = { name, origin, destination, speed, loop ->
            println("Saved: $name, $origin, $destination, $speed, $loop")
        }
    )
}

@Preview(showSystemUi = true, device = "id:pixel_8")
@Composable
fun EditSavedPlacePreview() {
    EditDialog(
        nickname = "Morning Route",
        onDismiss = {},
        onSave = { name: String -> }
    )
}