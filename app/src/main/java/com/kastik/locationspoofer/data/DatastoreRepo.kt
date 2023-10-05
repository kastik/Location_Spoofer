package com.kastik.locationspoofer.data

import android.content.Context
import androidx.core.content.edit
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlin.properties.Delegates







class DatastoreRepo private constructor(context: Context) {




    private val sharedPreferences =
        context.applicationContext.getSharedPreferences("USER_PREFERENCES", Context.MODE_PRIVATE)

    fun enableExposed(boolean: Boolean){
        sharedPreferences.edit{
            putBoolean("enableExposed",boolean)
        }
    }

    fun isExposedEnabled(): MutableStateFlow<Boolean>{
        return MutableStateFlow(sharedPreferences.getBoolean("enableExposed",false))
    }





    companion object {
        @Volatile
        private var INSTANCE: DatastoreRepo? = null
        fun getInstance(context: Context): DatastoreRepo {
            return INSTANCE ?: synchronized(this) {
                INSTANCE?.let {
                    return it
                }

                val instance = DatastoreRepo(context)
                INSTANCE = instance
                instance
            }
        }
    }
}