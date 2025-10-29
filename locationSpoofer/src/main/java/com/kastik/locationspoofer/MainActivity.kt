package com.kastik.locationspoofer

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.firebase.analytics.FirebaseAnalytics
import com.kastik.locationspoofer.ui.navigation.NavHost
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var analytics: FirebaseAnalytics

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @ExperimentalPermissionsApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analytics.logEvent(FirebaseAnalytics.Event.APP_OPEN,null)
        createNotificationChannel()
        setContent {
            NavHost()
        }
    }
}
