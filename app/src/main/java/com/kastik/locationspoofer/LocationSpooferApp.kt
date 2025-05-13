package com.kastik.locationspoofer

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LocationSpooferApp: Application() {
    override fun onCreate() {
        super.onCreate()
    }
}