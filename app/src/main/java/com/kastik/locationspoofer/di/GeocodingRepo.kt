package com.kastik.locationspoofer.di

import com.kastik.locationspoofer.data.api.GeocodingApi
import com.kastik.locationspoofer.data.api.RetrofitClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


class GeocodingRepo {
}
@Module
@InstallIn(SingletonComponent::class)
object GeocodingRepoModule {

    @Provides
    @Singleton
    fun provideGeocodingApi(): GeocodingApi {
        return RetrofitClient.geocodingApi
    }
}