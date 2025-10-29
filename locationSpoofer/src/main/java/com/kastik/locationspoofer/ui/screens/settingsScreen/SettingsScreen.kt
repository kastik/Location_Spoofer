package com.kastik.locationspoofer.ui.screens.settingsScreen

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kastik.locationspoofer.DarkMode
import com.kastik.locationspoofer.UserPreferences
import com.kastik.locationspoofer.ui.screens.settingsScreen.components.SettingsDropDownMenu
import com.kastik.locationspoofer.ui.screens.settingsScreen.components.SettingsSwitchItem
import com.kastik.locationspoofer.ui.theme.LocationSpooferTheme


@Composable
fun SettingsScreen() {
    val viewModel = hiltViewModel<SettingsScreenViewModel>()
    val preferencesState =
        viewModel.preferences.collectAsState(UserPreferences.newBuilder().build())
    SettingsScreenContent(
        preferences = preferencesState.value,
        onToggleMarkerZoom = { viewModel.enableMarkerZooming(it) },
        onToggleDarkMode = { viewModel.setDarkMode(it) },
        onToggleStatusBar = { viewModel.setStatusBar(it) })
}

@Composable
fun SettingsScreenContent(
    preferences: UserPreferences,
    onToggleMarkerZoom: (Boolean) -> Unit,
    onToggleDarkMode: (DarkMode) -> Unit,
    onToggleStatusBar: (Boolean) -> Unit
) {
    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(WindowInsets.systemBars.asPaddingValues())
                .padding(it),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            SettingsSwitchItem(
                title = "Enable Saved Routes on StatusBar",
                checked = preferences.enableStatusBarSavedRoutes,
                onCheckedChange = onToggleStatusBar
            )
            SettingsDropDownMenu(
                title = "Dark Mode", selected = preferences.darkMode, options = listOf(
                    DarkMode.followSystem to "Follow System",
                    DarkMode.darkMode to "Dark",
                    DarkMode.lightMode to "Light"
                ), onSelectedChange = { newMode -> onToggleDarkMode(newMode) })
        }
    }

}

@Preview(
    name = "Light Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun SettingsScreenPreview() {
    LocationSpooferTheme {
        SettingsScreenContent(
            preferences = UserPreferences.newBuilder()
                .setEnableXposed(false)
                .setDisableMarkerZooming(true)
                .setDarkMode(DarkMode.followSystem)
                .build(),
            onToggleMarkerZoom = {},
            onToggleDarkMode = {},
            onToggleStatusBar = {},
        )
    }
}


