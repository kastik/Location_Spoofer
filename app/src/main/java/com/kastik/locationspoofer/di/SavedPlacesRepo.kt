package com.kastik.locationspoofer.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import dagger.Module
import dagger.Provides
import com.kastik.locationspoofer.data.datastore.SavedPlacesRepo
import com.kastik.locationspoofer.data.datastore.PlacesSerializer
import com.kastik.locationspoofer.debug.SavedPlaces
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SavedPlacesModule {

    @Provides
    @Singleton
    fun provideSavedPlacesDataStore(@ApplicationContext context: Context): DataStore<SavedPlaces> {
        return DataStoreFactory.create(
            serializer = PlacesSerializer,
            produceFile = { context.dataStoreFile("saved_places.pb") }
        )
    }

    @Provides
    @Singleton
    fun provideSavedPlacesRepo(dataStore: DataStore<SavedPlaces>): SavedPlacesRepo {
        return SavedPlacesRepo(dataStore)
    }
}