package com.kastik.locationspoofer

import android.Manifest
import android.app.AppOpsManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import androidx.annotation.RequiresPermission
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Train
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.tasks.await

suspend fun getUserLocation(context: Context): LatLng? {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    // First try the cached last location
    val lastKnown = fusedLocationClient.lastLocation.await()
    if (lastKnown != null) {
        return LatLng(lastKnown.latitude, lastKnown.longitude)
    }

    // If lastLocation is null, actively request a fresh one
    val locationRequest =
        CurrentLocationRequest.Builder().setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setMaxUpdateAgeMillis(0) // ensure fresh data
            .setGranularity(Granularity.GRANULARITY_FINE).build()

    val current = fusedLocationClient.getCurrentLocation(locationRequest, null).await()

    return current?.let {
        LatLng(it.latitude, it.longitude)
    }
}

fun encodePolyline(points: List<LatLng>): String {
    var lastLat = 0
    var lastLng = 0
    val result = StringBuilder()

    for (point in points) {
        val lat = (point.latitude * 1e5).toInt()
        val lng = (point.longitude * 1e5).toInt()

        val dLat = lat - lastLat
        val dLng = lng - lastLng

        lastLat = lat
        lastLng = lng

        encode(dLat, result)
        encode(dLng, result)
    }

    return result.toString()
}

private fun encode(value: Int, result: StringBuilder) {
    var v = if (value < 0) (value shl 1).inv() else value shl 1
    while (v >= 0x20) {
        result.append(((0x20 or (v and 0x1f)) + 63).toChar())
        v = v shr 5
    }
    result.append((v + 63).toChar())
}



fun decodePolyline(encoded: String): List<LatLng> {
    val poly = mutableListOf<LatLng>()
    var index = 0
    val len = encoded.length
    var lat = 0
    var lng = 0

    while (index < len) {
        var result = 1
        var shift = 0
        var b: Int

        // Decode latitude
        do {
            b = encoded[index++].code - 63 - 1
            result += b shl shift
            shift += 5
        } while (b >= 0x1f)
        lat += if ((result and 1) != 0) (result shr 1).inv() else (result shr 1)

        // Decode longitude
        result = 1
        shift = 0
        do {
            b = encoded[index++].code - 63 - 1
            result += b shl shift
            shift += 5
        } while (b >= 0x1f)
        lng += if ((result and 1) != 0) (result shr 1).inv() else (result shr 1)

        poly.add(LatLng(lat / 1e5, lng / 1e5))
    }

    return poly
}


fun Context.createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            "running_channel", "Running Service", NotificationManager.IMPORTANCE_HIGH
        )
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}


fun isMockLocationApp(context: Context): Boolean {
    val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as? AppOpsManager
    return if (appOps != null ) {
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_MOCK_LOCATION, android.os.Process.myUid(), context.packageName
        )
        mode == AppOpsManager.MODE_ALLOWED
    } else {
        false
    }
}

fun areNotificationsEnabled(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        NotificationManagerCompat.from(context).areNotificationsEnabled()
    }
}


//TODO
object DeveloperOptionsHelper {
    fun isDeveloperOptionsEnabled(context: Context): Boolean {
        try {
            return Settings.Global.getInt(
                context.contentResolver, Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0
            ) != 0
        } catch (e: SettingNotFoundException) {
            e.printStackTrace()
            return false
        }
    }

    fun openDeveloperOptions(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    fun openSettings(context: Context) {
        val intent = Intent(Settings.ACTION_SETTINGS)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }
}

fun mapPlaceTypesToIcon(types: List<String>): ImageVector {
    return when {
        "restaurant" in types -> Icons.Filled.Restaurant
        "cafe" in types -> Icons.Filled.LocalCafe
        "bar" in types -> Icons.Filled.LocalBar
        "park" in types -> Icons.Filled.Park
        "hotel" in types || "lodging" in types -> Icons.Filled.Hotel
        "airport" in types -> Icons.Filled.Flight
        "train_station" in types -> Icons.Filled.Train
        "bus_station" in types -> Icons.Filled.DirectionsBus
        "shopping_mall" in types || "store" in types -> Icons.Filled.ShoppingCart
        "school" in types || "university" in types -> Icons.Filled.School
        "hospital" in types || "doctor" in types -> Icons.Filled.LocalHospital
        "locality" in types || "political" in types || "geocode" in types -> Icons.Filled.LocationCity
        else -> Icons.Filled.Place
    }
}
