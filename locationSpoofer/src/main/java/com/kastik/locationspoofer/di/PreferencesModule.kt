package com.kastik.locationspoofer.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.kastik.locationspoofer.data.datasource.local.UserPreferencesLocalDataSource
import com.kastik.locationspoofer.data.mapers.UserPreferencesSerializer
import com.kastik.locationspoofer.UserPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserPreferencesModule {

    @Provides
    @Singleton
    fun provideUserPreferencesDataStore(@ApplicationContext context: Context): DataStore<UserPreferences> {
        return DataStoreFactory.create(
            serializer = UserPreferencesSerializer,
            produceFile = { context.dataStoreFile("user_preferences.pb") }
        )
    }

    @Provides
    @Singleton
    fun provideUserPreferencesRepo(dataStore: DataStore<UserPreferences>): UserPreferencesLocalDataSource {
        return UserPreferencesLocalDataSource(dataStore)
    }
}