package com.kastik.locationspoofer.data.api

import com.kastik.locationspoofer.data.models.mapsAPI.geocode.GeocodingResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApi {
    @GET("geocode/json")
    suspend fun reverseGeocode(
        @Query("latlng") latlng: String,
        @Query("key") key: String,
        @Query("language") language: String? = null,
        @Query("region") region: String? = null,
        @Query("result_type") resultType: String? = null,
        @Query("location_type") locationType: String? = null
    ): GeocodingResponse
}