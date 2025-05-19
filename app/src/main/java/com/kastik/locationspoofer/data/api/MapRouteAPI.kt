package com.kastik.locationspoofer.data.api

import com.kastik.locationspoofer.data.models.mapsAPI.RouteRequest
import com.kastik.locationspoofer.data.models.mapsAPI.RouteResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface GoogleRoutesApi {
    @POST("directions/v2:computeRoutes")
    suspend fun getRoute(
        @Body request: RouteRequest,
        @Header("X-Goog-Api-Key") apiKey: String,
        @Header("X-Goog-FieldMask") fieldMask: String
    ): RouteResponse
}