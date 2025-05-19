package com.kastik.locationspoofer.data.models.mapsAPI

data class RouteRequest(
    val origin: Waypoint,
    val destination: Waypoint,
    val intermediates: List<Waypoint>? = null,
    val travelMode: RouteTravelMode? = RouteTravelMode.TRAVEL_MODE_UNSPECIFIED,
    val routingPreference: RoutingPreference? = RoutingPreference.ROUTING_PREFERENCE_UNSPECIFIED,
    val polylineQuality: PolylineQuality? = PolylineQuality.POLYLINE_QUALITY_UNSPECIFIED,
    val polylineEncoding: PolylineEncoding = PolylineEncoding.POLYLINE_ENCODING_UNSPECIFIED,
    val departureTime: String? = null,
    val arrivalTime: String? = null,
    val computeAlternativeRoutes: Boolean = false,
    val routeModifiers: List<RouteModifiers>? = null,
    val languageCode: String? = null,
    val regionCode: String? = null,
    val units: Units? = Units.UNITS_UNSPECIFIED,
    val optimizeWaypointOrder: Boolean = false,
    val requestedReferenceRoutes: List<ReferenceRoute>? = null,
    val extraComputations: List<ExtraComputation> = listOf<ExtraComputation>(ExtraComputation.EXTRA_COMPUTATION_UNSPECIFIED),
    val trafficModel: TrafficModel = TrafficModel.TRAFFIC_MODEL_UNSPECIFIED,
    val transitPreferences: TransitPreferences = TransitPreferences(),
)


data class Waypoint(
    val via: Boolean? = null,
    val vehicleStopover: Boolean? = null,
    val sideOfRoad: Boolean? = null,
    val location: Location,
    val placeId: String? = null,
    val address: String? = null
)

data class Location(
    val latLng: LatLng,
    val heading: Int? = null,
)

data class LatLng(
    val latitude: Double, val longitude: Double
)

data class RouteModifiers(
    val avoidTolls: Boolean,
    val avoidHighways: Boolean,
    val avoidFerries: Boolean,
    val avoidIndoor: Boolean,
    val vehicleInfo: VehicleInfo,
    val tollPasses: TollPass = TollPass.TOLL_PASS_UNSPECIFIED
)

data class VehicleInfo(
    val emissionType: VehicleEmissionType = VehicleEmissionType.VEHICLE_EMISSION_TYPE_UNSPECIFIED
)

data class TransitPreferences(
    val allowedTravelModes: List<TransitTravelMode> = listOf<TransitTravelMode>(TransitTravelMode.TRANSIT_TRAVEL_MODE_UNSPECIFIED),
    val routingPreference: TransitRoutingPreference = TransitRoutingPreference.TRANSIT_ROUTING_PREFERENCE_UNSPECIFIED
)

enum class RouteTravelMode {
    TRAVEL_MODE_UNSPECIFIED, DRIVE, BICYCLE, WALK, TWO_WHEELER, TRANSIT
}

enum class RoutingPreference {
    ROUTING_PREFERENCE_UNSPECIFIED, TRAFFIC_UNAWARE, TRAFFIC_AWARE, TRAFFIC_AWARE_OPTIMAL
}

enum class PolylineQuality {
    POLYLINE_QUALITY_UNSPECIFIED, HIGH_QUALITY, OVERVIEW
}

enum class PolylineEncoding {
    POLYLINE_ENCODING_UNSPECIFIED, ENCODED_POLYLINE, GEO_JSON_LINESTRING,
}


enum class VehicleEmissionType {
    VEHICLE_EMISSION_TYPE_UNSPECIFIED, GASOLINE, ELECTRIC, HYBRID, DIESEL
}

//TODO There are more
enum class TollPass {
    TOLL_PASS_UNSPECIFIED,
}

enum class Units {
    UNITS_UNSPECIFIED, METRIC, IMPERIAL
}

enum class ReferenceRoute {
    REFERENCE_ROUTE_UNSPECIFIED, FUEL_EFFICIENT, SHORTER_DISTANCE
}

enum class ExtraComputation {
    EXTRA_COMPUTATION_UNSPECIFIED, TOLLS, FUEL_CONSUMPTION, TRAFFIC_ON_POLYLINE, HTML_FORMATTED_NAVIGATION_INSTRUCTIONS, FLYOVER_INFO_ON_POLYLINE, NARROW_ROAD_INFO_ON_POLYLINE
}

enum class TrafficModel {
    TRAFFIC_MODEL_UNSPECIFIED, BEST_GUESS, PESSIMISTIC, OPTIMISTIC
}


enum class TransitTravelMode {
    TRANSIT_TRAVEL_MODE_UNSPECIFIED, BUS, SUBWAY, TRAIN, LIGHT_RAIL, RAIL
}

enum class TransitRoutingPreference {
    TRANSIT_ROUTING_PREFERENCE_UNSPECIFIED, LESS_WALKING, FEWER_TRANSFERS
}