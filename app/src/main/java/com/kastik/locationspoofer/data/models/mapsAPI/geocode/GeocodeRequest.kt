package com.kastik.locationspoofer.data.models.mapsAPI.geocode

data class ReverseGeocodeRequest(
    val latlng: String,          // format: "latitude,longitude"
    val key: String,             // your API key
    val language: String? = null,   // optional, e.g. "en"
    val region: String? = null,     // optional, e.g. "US"
    val result_type: String? = null, // optional, comma-separated types
    val location_type: String? = null // optional, comma-separated location types
)