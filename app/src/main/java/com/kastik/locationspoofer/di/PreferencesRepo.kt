package com.kastik.locationspoofer.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.google.maps.places.v1.PlacesGrpc
import com.google.maps.routing.v2.RoutesGrpc
import com.kastik.locationspoofer.data.datastore.UserPreferencesRepo
import com.kastik.locationspoofer.data.datastore.UserPreferencesSerializer
import com.kastik.locationspoofer.UserPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.grpc.CallOptions
import io.grpc.Channel
import io.grpc.ClientCall
import io.grpc.ClientCall.Listener
import io.grpc.ClientInterceptor
import io.grpc.ClientInterceptors
import io.grpc.ForwardingClientCall
import io.grpc.ManagedChannelBuilder
import io.grpc.Metadata
import io.grpc.MethodDescriptor
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
    fun provideUserPreferencesRepo(dataStore: DataStore<UserPreferences>): UserPreferencesRepo {
        return UserPreferencesRepo(dataStore)
    }
}