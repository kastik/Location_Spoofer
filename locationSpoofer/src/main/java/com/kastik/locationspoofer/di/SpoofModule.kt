package com.kastik.locationspoofer.di

import android.content.Context
import com.kastik.locationspoofer.data.datasource.local.SpoofDataSource
import com.kastik.locationspoofer.data.datasource.local.SpoofDataSourceImpl
import com.kastik.locationspoofer.data.repository.SpoofRepositoryImpl
import com.kastik.locationspoofer.domain.repository.SpoofRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SpoofModule {

    @Provides
    @Singleton
    fun provideMockingDataSource(
        @ApplicationContext context: Context,
    ): SpoofDataSource = SpoofDataSourceImpl(context)

    @Provides
    @Singleton
    fun provideLocationRepository(
        spoofDataSource: SpoofDataSource
    ): SpoofRepository = SpoofRepositoryImpl(spoofDataSource)

}