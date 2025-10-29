package com.kastik.locationspoofer.ui.screens.settingsScreen.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kastik.locationspoofer.ui.theme.LocationSpooferTheme

@Composable
fun SettingsSwitchItem(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        headlineContent = {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
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
fun SettingsSwitchItemPreview() {
    LocationSpooferTheme {
        SettingsSwitchItem(
            title = "Some setting",
            checked = true,
            onCheckedChange = {}
        )
    }
}