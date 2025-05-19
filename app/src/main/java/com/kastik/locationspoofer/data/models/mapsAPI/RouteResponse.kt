package com.kastik.locationspoofer.data.models.mapsAPI

import com.google.protobuf.Duration
import com.google.protobuf.Timestamp

data class RouteResponse(
    val routes: List<Route>, val fallbackInfo: FallbackInfo, val geocodingResults: GeocodingResults
)

data class Route(
    val routeLabels: List<RouteLabel>,
    val legs: List<RouteLeg>,
    val distanceMeters: Int,
    val duration: String,
    val staticDuration: String,
    val polyline: Polyline,
    val description: String,
    val warnings: List<String>,
    val viewport: Viewport,
    val travelAdvisory: RouteTravelAdvisory,
    val optimizedIntermediateWaypointIndex: List<Int>,
    val localizedValues: RouteLocalizedValues,
    val routeToken: String,
    val polylineDetails: PolylineDetails
)

data class FallbackInfo(
    val routingMode: FallbackRoutingMode, val reason: FallbackReason

)

data class GeocodingResults(
    val origin: GeocodedWaypoint,
    val destination: GeocodedWaypoint,
    val intermediates: GeocodedWaypoint,
)

data class GeocodedWaypoint(
    val geocoderStatus: Status,
    val type: List<String>,
    val partialMatch: Boolean,
    val placeId: String,
    val intermediateWaypointRequestIndex: Int
)

data class Status(
    val code: Int, val message: String, val details: List<Map<String, String>>
)

data class Polyline(
    val encodedPolyline: String, val geoJsonLinestring: Any
)

data class RouteLocalizedValues(
    val distance: LocalizedText,
    val duration: LocalizedText,
    val staticDuration: LocalizedText,
    val transitFare: LocalizedText,
)

data class LocalizedText(
    val text: String, val languageCode: String
)

data class PolylineDetails(
    val flyoverInfo: FlyoverInfo, val narrowRoadInfo: NarrowRoadInfo
)

data class FlyoverInfo(
    val flyoverPresence: RoadFeatureState, val polylinePointIndex: PolylinePointIndex
)


data class PolylinePointIndex(
    val startIndex: Int, val endIndex: Int
)

data class NarrowRoadInfo(
    val narrowRoadPresence: RoadFeatureState, val polylinePointIndex: PolylinePointIndex
)

data class RouteTravelAdvisory(
    val tollInfo: TollInfo, val speedReadingIntervals: List<SpeedReadingInterval>
)

data class TollInfo(
    val estimatedPrice: Money
)

data class Money(
    val currencyCode: String, val units: String, val nanos: Int
)


data class Viewport(
    val low: LatLng, val high: LatLng
)

data class RouteLeg(
    val distanceMeters: Int,
    val duration: String,
    val staticDuration: String,
    val polyline: Polyline,
    val startLocation: Location,
    val endLocation: Location,
    val steps: List<RouteLegStep>,
    val travelAdvisory: RouteLegTravelAdvisory,
)

data class RouteLegTravelAdvisory(
    val tollInfo: TollInfo,
    val speedReadingIntervals: List<SpeedReadingInterval>,
)


data class RouteLegStep(
    val distanceMeters: Int,
    val staticDuration: String,
    val polyline: Polyline,
    val startLocation: Location,
    val endLocation: Location,
    val navigationInstruction: NavigationInstruction,
    val travelAdvisory: RouteLegStepTravelAdvisory,
    val localizedValues: RouteLegStepLocalizedValues,
    val transitDetails: RouteLegStepTransitDetails,
    val travelMode: RouteTravelMode
)

data class RouteLegStepLocalizedValues(
    val distance: LocalizedText, val staticDuration: LocalizedText
)

data class RouteLegStepTransitDetails(
    val stopDetails: TransitStopDetails,
    val localizedValues: TransitDetailsLocalizedValues,
    val headsign: String,
    val headway: Duration,
    val transitLine: TransitLine,
    val stopCount: Int,
    val tripShortText: String,
)

data class TransitLine(
    val agencies: List<TransitAgency>,
    val name: String,
    val uri: String,
    val color: String,
    val iconUri: String,
    val nameShort: String,
    val textColor: String,
    val vehicle: TransitVehicle
)

data class TransitVehicle(
    val name: LocalizedText,
    val type: TransitVehicleType = TransitVehicleType.TRANSIT_VEHICLE_TYPE_UNSPECIFIED,
    val iconUri: String,
    val localIconUri: String
)

data class TransitAgency(
    val name: String, val phoneNumber: String, val uri: String
)


data class TransitStopDetails(
    val arrivalStop: TransitStop,
    val arrivalTime: Timestamp,
    val departureStop: TransitStop,
    val departureTime: Timestamp,
)

data class TransitStop(
    val name: String, val location: Location
)

data class TransitDetailsLocalizedValues(
    val arrivalTime: LocalizedTime, val departureTime: LocalizedTime
)

data class LocalizedTime(
    val time: LocalizedText, val timeZone: String
)


data class NavigationInstruction(
    val maneuver: Maneuver, val instructions: String
)

data class RouteLegStepTravelAdvisory(
    val speedReadingIntervals: List<SpeedReadingInterval>
)

data class SpeedReadingInterval(
    val startPolylinePointIndex: Int, val endPolylinePointIndex: Int, val speed: Speed
)

enum class TransitVehicleType {
    TRANSIT_VEHICLE_TYPE_UNSPECIFIED, BUS, CABLE_CAR, COMMUTER_TRAIN, FERRY, FUNICULAR, GONDOLA_LIFT, HEAVY_RAIL, HIGH_SPEED_TRAIN, INTERCITY_BUS, LONG_DISTANCE_TRAIN, METRO_RAIL, MONORAIL, OTHER, RAIL, SHARE_TAXI, SUBWAY, TRAM, TROLLEYBUS
}


enum class Speed {
    SPEED_UNSPECIFIED, NORMAL, SLOW, TRAFFIC_JAM
}

enum class Maneuver {
    MANEUVER_UNSPECIFIED, TURN_SLIGHT_LEFT, TURN_SHARP_LEFT, UTURN_LEFT, TURN_LEFT, TURN_SLIGHT_RIGHT, TURN_SHARP_RIGHT, UTURN_RIGHT, TURN_RIGHT, STRAIGHT, RAMP_LEFT, RAMP_RIGHT, MERGE, FORK_LEFT, FORK_RIGHT, FERRY, FERRY_TRAIN, ROUNDABOUT_LEFT, ROUNDABOUT_RIGHT, DEPART, NAME_CHANGE
}

enum class RouteLabel {
    ROUTE_LABEL_UNSPECIFIED, DEFAULT_ROUTE, DEFAULT_ROUTE_ALTERNATE, FUEL_EFFICIENT, SHORTER_DISTANCE
}

enum class RoadFeatureState {
    ROAD_FEATURE_STATE_UNSPECIFIED, EXISTS, DOES_NOT_EXIST
}


enum class FallbackRoutingMode {
    FALLBACK_ROUTING_MODE_UNSPECIFIED, FALLBACK_TRAFFIC_UNAWARE, FALLBACK_TRAFFIC_AWARE
}

enum class FallbackReason {
    FALLBACK_REASON_UNSPECIFIED, SERVER_ERROR, LATENCY_EXCEEDED
}