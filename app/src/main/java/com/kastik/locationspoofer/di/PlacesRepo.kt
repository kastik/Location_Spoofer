package com.kastik.locationspoofer.di

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.google.maps.places.v1.PlacesGrpc
import com.google.maps.routing.v2.RoutesGrpc
import com.kastik.locationspoofer.BuildConfig
import com.kastik.locationspoofer.SavedPlaces
import dagger.Module
import dagger.Provides
import com.kastik.locationspoofer.data.datastore.SavedPlacesRepo
import com.kastik.locationspoofer.data.datastore.PlacesSerializer
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
import io.grpc.ForwardingClientCallListener
import io.grpc.ManagedChannelBuilder
import io.grpc.Metadata
import io.grpc.MethodDescriptor
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


    @Provides
    @Singleton
    fun providePlacesStub(): PlacesGrpc.PlacesBlockingStub {
        val apiKey = BuildConfig.PLACES_API_KEY

        val channel = ManagedChannelBuilder
            .forTarget("places.googleapis.com:443")
            .useTransportSecurity()
            .build()

        val interceptor = object : ClientInterceptor {
            private val apiKeyHeader = Metadata.Key.of("x-goog-api-key", Metadata.ASCII_STRING_MARSHALLER)
            private val fieldMaskHeader = Metadata.Key.of("X-Goog-FieldMask", Metadata.ASCII_STRING_MARSHALLER)

            override fun <ReqT, RespT> interceptCall(
                method: MethodDescriptor<ReqT, RespT>,
                callOptions: CallOptions,
                next: Channel
            ): ClientCall<ReqT, RespT> {
                val call = next.newCall(method, callOptions)
                return object : ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(call) {
                    override fun start(responseListener: Listener<RespT>, headers: Metadata) {
                        headers.put(apiKeyHeader, apiKey)
                        headers.put(fieldMaskHeader, "*") // TODO: still better to use FieldMask in request
                        val loggingListener = object : ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(responseListener) {
                            override fun onMessage(message: RespT) {
                                Log.d("MyLog","\ngRPC Response: \n$message")
                                super.onMessage(message)
                            }
                        }
                        super.start(loggingListener, headers)
                    }
                }
            }
        }

        val interceptedChannel = ClientInterceptors.intercept(channel, interceptor)

        return PlacesGrpc.newBlockingStub(interceptedChannel)
    }
}