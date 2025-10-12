package com.kastik.locationspoofer.ui.screens

import android.util.Log
import androidx.navigation.NavType
import com.google.maps.routing.v2.Route
import kotlinx.serialization.Contextual
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.Json

object MapRouteSerializer : KSerializer<MapRoute> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("MapRoute") {
        element("route", ByteArraySerializer().descriptor, isOptional = true)
    }

    override fun serialize(encoder: Encoder, value: MapRoute) {
        encoder.encodeStructure(descriptor) {
            if (value.route != null) {
                val bytes = value.route.toByteArray()
                encodeSerializableElement(descriptor, 0, ByteArraySerializer(), bytes)
            }
        }
    }

    override fun deserialize(decoder: Decoder): MapRoute {
        return decoder.decodeStructure(descriptor) {
            var route: Route? = null
            if (decodeElementIndex(descriptor) != CompositeDecoder.DECODE_DONE) {
                val bytes = decodeSerializableElement(descriptor, 0, ByteArraySerializer())
                route = Route.parseFrom(bytes)
            }
            MapRoute(route)
        }
    }
}

object MapRouteNavType : NavType<MapRoute>(isNullableAllowed = true) {
    override fun get(bundle: android.os.Bundle, key: String): MapRoute? {
        Log.d("MyLog","get route")
        return bundle.getString(key)?.let { Json.decodeFromString(it) }
    }

    override fun parseValue(value: String): MapRoute {
        Log.d("MyLog","dec route")

        return Json.decodeFromString(value)
    }

    override fun put(bundle: android.os.Bundle, key: String, value: MapRoute) {
        Log.d("MyLog","put route")

        bundle.putString(key, Json.encodeToString(value))
    }

    override fun serializeAsValue(value: MapRoute): String {
        Log.d("MyLog","ser route")
        return Json.encodeToString(value)
    }
}

@Serializable(with = MapRouteSerializer::class)
data class MapRoute(
    val route: Route? = null
)

@Serializable
object SavedRoutesRoute

@Serializable
object SettingsRoute