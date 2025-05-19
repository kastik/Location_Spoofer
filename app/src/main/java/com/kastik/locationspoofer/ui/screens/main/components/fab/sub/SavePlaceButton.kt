package com.kastik.locationspoofer.ui.screens.main.components.fab.sub

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import com.kastik.locationspoofer.data.models.MarkerData
import com.kastik.locationspoofer.debug.LatLng
import com.kastik.locationspoofer.debug.Place
import kotlinx.coroutines.flow.StateFlow


@Composable
fun SaveLocationButton(
    marker: MarkerData?,
    savePlace: (place: Place) -> Unit,
    isPlaceSaved: State<Boolean>,
    removeSavedPlace: () -> Unit
) {
    AnimatedVisibility(
        visible = marker?.name != null,
        enter = scaleIn(),
        exit = scaleOut()) {
        FloatingActionButton(onClick = {
            if (isPlaceSaved.value){
                Log.d("MyLog","If in onClick")
                removeSavedPlace()
            }else {
                Log.d("MyLog","Else in onClick")
                savePlace(
                    Place.newBuilder()
                        .setPlaceId(marker?.placeId)
                        .setLatLng(
                            LatLng.newBuilder()
                                .setLatitude(marker?.latLng?.latitude ?: 0.0)
                                .setLongitude(marker?.latLng?.longitude ?: 0.0)
                        )
                        .setPlacePrimaryText(marker?.name)
                        .build()
                )
            }
        }, content = {
            if (isPlaceSaved.value) {
                Log.d("MyLog","IF in content")
                Icon(Icons.Default.Bookmark, "Save this location")
            }else{
                Log.d("MyLog","Else in content")
                Icon(Icons.Default.RemoveCircleOutline,"Remove place from saved")
            }
        })
    }
}