package com.kastik.locationspoofer.ui.navigation

import kotlinx.serialization.Serializable


@Serializable
data class MapRoute(
    //Necessary as passing NavType with Proto is currently buggy
    val serializedRoute: String? = null,
    val serializedPlace: String? = null
)

@Serializable
object SavedRoutesRoute

@Serializable
object SettingsRoute