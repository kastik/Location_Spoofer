package com.kastik.locationspoofer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.kastik.locationspoofer.data.MyViewModel
import com.kastik.locationspoofer.ui.screens.UIStuff


private val Context.dataStore by preferencesDataStore("settings")

class MainActivity : ComponentActivity() {


    private val viewModel = MyViewModel()

    private  val serviceBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val number = intent.getBooleanExtra("SUCCESS",false)
            Log.d("MyLog","Got brodcast with val $number")
            viewModel.showMockPermissionErrorDialog()
        }
    }



    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @ExperimentalPermissionsApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intentFilter = IntentFilter("MOCK")
        ContextCompat.registerReceiver(this,serviceBroadcastReceiver, intentFilter, ContextCompat.RECEIVER_NOT_EXPORTED)


        setContent {
            UIStuff(viewModel)
        }
    }

    override fun onDestroy() {
        unregisterReceiver(serviceBroadcastReceiver)
        super.onDestroy()
    }
}




