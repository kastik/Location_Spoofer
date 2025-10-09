package com.kastik.locationspoofer.ui.screens.mapScreen.components.fab.sub

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.maps.model.LatLng
import com.kastik.locationspoofer.service.LocationMockServiceState
import com.kastik.locationspoofer.data.models.MarkerData
import com.kastik.locationspoofer.data.models.SpoofFabData
import com.kastik.locationspoofer.ui.screens.mapScreen.MapScreenState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationSpoofButton(
    serviceState: LocationMockServiceState,
    hasPlacedMarkerOrPolyline: Boolean,
    stopSpoofing: () -> Unit,
    startSpoofing: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    AnimatedVisibility(
        hasPlacedMarkerOrPolyline || serviceState is LocationMockServiceState.MockingLocation,
        enter = scaleIn(),
        exit = scaleOut()
    ) {
        val spoofFabData: SpoofFabData = when {
            serviceState is LocationMockServiceState.MockingLocation -> {
                SpoofFabData(
                    Icons.Default.Close, {
                        scope.launch {
                            stopSpoofing()
                        }
                    })
            }

            else -> SpoofFabData(
                Icons.Default.VisibilityOff, {
                    startSpoofing()
                })
        }

        spoofFabData.icon?.let {
            FloatingActionButton(
                modifier = Modifier.Companion.padding(top = 6.dp), onClick = spoofFabData.onClick
            ) {
                AnimatedContent(
                    targetState = spoofFabData.icon, transitionSpec = {
                        fadeIn(animationSpec = tween(300)).togetherWith(
                            fadeOut(
                                animationSpec = tween(
                                    300
                                )
                            )
                        )
                    }, label = "IconTransition"
                ) { targetIcon ->
                    Icon(targetIcon, contentDescription = "Spoof Control")
                }
            }
        }
    }
}