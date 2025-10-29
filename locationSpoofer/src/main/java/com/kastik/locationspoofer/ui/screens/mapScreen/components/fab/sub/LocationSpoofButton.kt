package com.kastik.locationspoofer.ui.screens.mapScreen.components.fab.sub

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationSpoofButton(
    isSpoofing: Boolean,
    hasPlacedMarkerOrPolyline: Boolean,
    stopSpoofing: () -> Unit,
    startSpoofing: () -> Unit,
) {
    AnimatedVisibility(
        visible = hasPlacedMarkerOrPolyline || isSpoofing,
        enter = scaleIn(),
        exit = scaleOut()
    ) {
        FloatingActionButton(
            modifier = Modifier.padding(top = 6.dp),
            onClick = {
                if (isSpoofing) {
                    stopSpoofing()
                } else {
                    startSpoofing()
                }
            }, content = {
                AnimatedContent(
                    targetState = isSpoofing, transitionSpec = {
                        ContentTransform(
                            targetContentEnter = slideInVertically(
                                animationSpec = spring(
                                    stiffness = Spring.StiffnessLow,
                                    dampingRatio = Spring.DampingRatioHighBouncy
                                ), initialOffsetY = { -it }), initialContentExit = fadeOut(
                                animationSpec = tween(100)
                            )
                        )
                    }, label = "IconTransition"
                ) { targetIcon ->
                    if (targetIcon) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Save this location"
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.VisibilityOff,
                            contentDescription = "Remove place from saved"
                        )
                    }
                }
            })
    }
}