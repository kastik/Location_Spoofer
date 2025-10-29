package com.kastik.locationspoofer.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.google.maps.routing.v2.RoutesGrpc
import com.kastik.locationspoofer.BuildConfig
import com.kastik.locationspoofer.SavedRoutes
import com.kastik.locationspoofer.data.datasource.local.RouteLocalDataSource
import com.kastik.locationspoofer.data.mapers.SavedRouteSerializer
import com.kastik.locationspoofer.data.datasource.remote.RoutesRemoteDataSource
import com.kastik.locationspoofer.data.repository.RoutesRepositoryImpl
import com.kastik.locationspoofer.domain.repository.RoutesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.grpc.CallOptions
import io.grpc.Channel
import io.grpc.ClientCall
import io.grpc.ClientInterceptor
import io.grpc.ForwardingClientCall
import io.grpc.ManagedChannelBuilder
import io.grpc.MethodDescriptor
import javax.inject.Singleton
import io.grpc.*

@Module
@InstallIn(SingletonComponent::class)
object SavedRoutesModule {


    @Provides
    @Singleton
    fun provideRoutesRemoteDataSource(
        stub: RoutesGrpc.RoutesBlockingStub
    ): RoutesRemoteDataSource = RoutesRemoteDataSource(stub)

    @Provides
    @Singleton
    fun provideRoutesDataStore(@ApplicationContext context: Context): DataStore<SavedRoutes> {
        return DataStoreFactory.create(
            serializer = SavedRouteSerializer,
            produceFile = { context.dataStoreFile("saved_routes.pb") }
        )
    }

    @Provides
    @Singleton
    fun provideRouteLocalDataSource(dataStore: DataStore<SavedRoutes>): RouteLocalDataSource {
        return RouteLocalDataSource(dataStore)
    }

    @Provides
    @Singleton
    fun provideRoutesRepository(
        remote: RoutesRemoteDataSource,
        local: RouteLocalDataSource
    ): RoutesRepository = RoutesRepositoryImpl(local, remote)


    @Provides
    @Singleton
    fun provideRoutesStub(): RoutesGrpc.RoutesBlockingStub {
        val apiKey = BuildConfig.ROUTES_API_KEY

        val channel = ManagedChannelBuilder
            .forTarget("routes.googleapis.com:443")
            .useTransportSecurity()
            .build()

        // Define the interceptor inline
        val interceptor = object : ClientInterceptor {
            private val apiKeyHeader = Metadata.Key.of("x-goog-api-key", Metadata.ASCII_STRING_MARSHALLER)
            private val fieldMaskHeader = Metadata.Key.of("x-goog-fieldmask", Metadata.ASCII_STRING_MARSHALLER)

            override fun <ReqT, RespT> interceptCall(
                method: MethodDescriptor<ReqT, RespT>,
                callOptions: CallOptions,
                next: Channel
            ): ClientCall<ReqT, RespT> {
                val call = next.newCall(method, callOptions)
                return object : ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(call) {
                    override fun start(responseListener: Listener<RespT>, headers: Metadata) {
                        headers.put(apiKeyHeader, apiKey)
                        headers.put(fieldMaskHeader, "*") //TODO
                        super.start(responseListener, headers)
                    }
                }
            }
        }

        val interceptedChannel = ClientInterceptors.intercept(channel, interceptor)

        return RoutesGrpc.newBlockingStub(interceptedChannel)
    }
}