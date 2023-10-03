package com.kastik.locationspoofer

import android.content.Context
import android.content.Intent
import android.util.Log
import com.kastik.locationspoofer.data.MyViewModel

fun startSpoofService(viewModel: MyViewModel, context: Context) {

    if (viewModel.floatingIconStart().value) {
        val myIntent = Intent(context, UpdateLocationService::class.java)
        myIntent.putExtra(
            UpdateLocationService.Coordinates.Latitude.name,
            viewModel.getMarker().value?.latitude
        )
        myIntent.putExtra(
            UpdateLocationService.Coordinates.Longitude.name,
            viewModel.getMarker().value?.longitude
        )
        //myIntent.putExtra(UpdateLocationService.Coordinates.Altitude.name, viewModel.userAlt())
        myIntent.action = UpdateLocationService.ACTIONS.START.name
        viewModel.enableSpoofing()
        context.startService(myIntent)
    } else {
        val myIntent = Intent(context, UpdateLocationService::class.java)
        myIntent.action = UpdateLocationService.ACTIONS.STOP.name
        viewModel.disableSpoofing()
        context.startService(myIntent)
    }
}

fun searchQuery() {


}