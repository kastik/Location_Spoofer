package com.kastik.locationspoofer.data.models.mapsAPI.geocode

data class GeocodingResponse(
    val plus_code: PlusCode? = null,
    val results: List<Result> = emptyList(),
    val status: String
)

data class PlusCode(
    val compound_code: String? = null,
    val global_code: String
)

data class Result(
    val address_components: List<AddressComponent> = emptyList(),
    val formatted_address: String,
    val geometry: Geometry,
    val place_id: String,
    val plus_code: PlusCode? = null,
    val types: List<String> = emptyList()
)

data class AddressComponent(
    val long_name: String,
    val short_name: String,
    val types: List<String> = emptyList()
)

data class Geometry(
    val bounds: Bounds? = null,
    val location: LatLng,
    val location_type: String,
    val viewport: Bounds
)

data class Bounds(
    val northeast: LatLng,
    val southwest: LatLng
)

data class LatLng(
    val lat: Double,
    val lng: Double
)