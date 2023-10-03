package com.kastik.locationspoofer

import android.app.Service
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.location.provider.ProviderProperties
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import kotlin.concurrent.thread

class UpdateLocationService : Service() {
    private val mainHandler = Handler(Looper.getMainLooper())
    private lateinit var runnableJob: Runnable
    private var isRunning : Boolean = false


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        thread {
            if (isRunning) {
                when (intent.action) {
                    ACTIONS.START.name -> {
                        setLocation(
                            intent.getDoubleExtra(Coordinates.Latitude.name, 0.0),
                            intent.getDoubleExtra(Coordinates.Longitude.name, 0.0),
                            intent.getDoubleExtra(Coordinates.Altitude.name, 0.0)
                        )
                    }

                    ACTIONS.STOP.name -> {
                        stop()
                        stopSelf()
                    }
                }
            } else {
                stop()
                setLocation(
                    intent.getDoubleExtra(Coordinates.Latitude.name, 0.0),
                    intent.getDoubleExtra(Coordinates.Longitude.name, 0.0),
                    intent.getDoubleExtra(Coordinates.Altitude.name, 0.0)
                )
            }
        }

        return START_STICKY
        //super.onStartCommand(intent, flags, startId)
    }


    private fun createNotification() {
        val notification =
            NotificationCompat.Builder(this, "running_channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("My notification")
                .setContentText("Much longer text that cannot fit one line...")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()
        startForeground(1, notification)
    }


    @RequiresApi(Build.VERSION_CODES.S)
    private fun setLocation(lat: Double, lon: Double, alt: Double) {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        if (this::runnableJob.isInitialized) {
            mainHandler.removeCallbacks(runnableJob)
        }

        try {
            locationManager.addTestProvider(
                LocationManager.GPS_PROVIDER,
                false,
                false,
                false,
                false,
                true,
                true,
                true,
                ProviderProperties.POWER_USAGE_LOW,
                ProviderProperties.ACCURACY_FINE
            )
            val location = Location(LocationManager.GPS_PROVIDER)

            runnableJob = object : Runnable {
                override fun run() {
                    location.latitude = lat
                    location.longitude = lon
                    location.setAltitude(10.0)
                    location.time = System.currentTimeMillis()
                    location.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
                    location.setAccuracy(5F)
                    locationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, location)
                    mainHandler.postDelayed(this, 100)
                }
            }
            locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true)
            mainHandler.post(runnableJob)
            createNotification()
        } catch (e: SecurityException) {

            val broadIntent = Intent("MOCK")
            broadIntent.putExtra("SUCCESS",false)
            Log.d("MyLog","Sent brodcast")
            sendBroadcast(broadIntent)

            stop()
            stopSelf()

        }
    }


    private fun stop() {
        if (!isRunning) {
            mainHandler.removeCallbacks(runnableJob)
        }
    }


    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    enum class ACTIONS {
        START,
        STOP
    }


    enum class Coordinates {
        Longitude,
        Latitude,
        Altitude
    }

    override fun onCreate() {
        super.onCreate()
        isRunning = true
    }

    override fun onDestroy() {
        isRunning = false
        super.onDestroy()
    }

}

