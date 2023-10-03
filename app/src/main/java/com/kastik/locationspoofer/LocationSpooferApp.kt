package com.kastik.locationspoofer

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import com.google.accompanist.permissions.PermissionState
import com.google.android.gms.location.LocationServices

class LocationSpooferApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val MyChannel = NotificationChannel("running_channel", "Running Service", NotificationManager.IMPORTANCE_HIGH)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(MyChannel)
        }
    }
}