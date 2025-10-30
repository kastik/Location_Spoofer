package com.kastik.locationspoofer.di

import com.kastik.locationspoofer.domain.repository.SpoofRepository
import com.kastik.locationspoofer.domain.repository.PlacesRepository
import com.kastik.locationspoofer.domain.repository.RoutesRepository
import com.kastik.locationspoofer.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCasesModule {

    // Places
    @Provides @Singleton
    fun provideGetSavedPlacesUseCase(repo: PlacesRepository) =
        GetSavedPlacesUseCase(repo)

    @Provides @Singleton
    fun provideSavePlaceUseCase(repo: PlacesRepository) =
        SavePlaceUseCase(repo)

    @Provides @Singleton
    fun provideDeletePlaceUseCase(repo: PlacesRepository) =
        DeletePlaceUseCase(repo)

    @Provides @Singleton
    fun provideSearchPlacesUseCase(repo: PlacesRepository) =
        SearchPlacesUseCase(repo)

    @Provides @Singleton
    fun provideUpdatePlaceUseCase(repo: PlacesRepository) =
        UpdatePlaceUseCase(repo)

    @Provides @Singleton
    fun provideCheckIfPlaceIsSavedUseCase(repo: PlacesRepository) =
        CheckIfPlaceIsSavedUseCase(repo)

    @Provides @Singleton
    fun provideGetPlaceDetailsWithIdUseCase(repo: PlacesRepository) =
        GetPlaceDetailsWithId(repo)

    // Routes
    @Provides @Singleton
    fun provideGetSavedRoutesUseCase(repo: RoutesRepository) =
        GetSavedRoutesUseCase(repo)

    @Provides @Singleton
    fun provideSaveRouteUseCase(repo: RoutesRepository) =
        SaveRouteUseCase(repo)

    @Provides @Singleton
    fun provideDeleteRouteUseCase(repo: RoutesRepository) =
        DeleteRouteUseCase(repo)

    @Provides @Singleton
    fun provideComputeRouteUseCase(repo: RoutesRepository) =
        ComputeRouteUseCase(repo)

    @Provides @Singleton
    fun provideUpdateRouteUseCase(repo: RoutesRepository) =
        UpdateRouteUseCase(repo)

    @Provides @Singleton
    fun provideCheckIfRouteIsSavedUseCase(repo: RoutesRepository) =
        CheckIfRouteIsSavedUseCase(repo)

    @Provides @Singleton
    fun provideStartMockLocationUseCase(repo: SpoofRepository) =
        StartSpoofingUseCase(repo)

    @Provides @Singleton
    fun provideStopMockLocationUseCase(repo: SpoofRepository) =
        StopSpoofingUseCase(repo)

    @Provides @Singleton
    fun provideGetMockingStateUseCase(repo: SpoofRepository) =
        GetSpoofingStateUseCase(repo)

    @Provides @Singleton
    fun provideEmulateRouteUseCase(): EmulateRouteUseCase = EmulateRouteUseCase()

    @Provides @Singleton
    fun provideEmulateLatLngUseCase(): EmulateLatLngUseCase = EmulateLatLngUseCase()

}