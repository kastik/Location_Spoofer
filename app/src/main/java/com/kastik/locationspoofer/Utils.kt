package com.kastik.locationspoofer

import android.app.AppOpsManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import com.google.android.gms.maps.model.LatLng


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
    return if (appOps != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_MOCK_LOCATION, android.os.Process.myUid(), context.packageName
        )
        mode == AppOpsManager.MODE_ALLOWED
    } else {
        false
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
