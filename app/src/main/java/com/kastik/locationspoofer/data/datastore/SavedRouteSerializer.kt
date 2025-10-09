package com.kastik.locationspoofer.data.datastore

import androidx.datastore.core.Serializer
import com.kastik.locationspoofer.SavedPlaces
import com.kastik.locationspoofer.SavedRoutes
import com.kastik.locationspoofer.UserPreferences
import java.io.InputStream
import java.io.OutputStream

object SavedRouteSerializer : Serializer<SavedRoutes> {
    override val defaultValue: SavedRoutes = SavedRoutes.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): SavedRoutes {
        return try {
            SavedRoutes.parseFrom(input)
        }catch (_ : Exception){
            SavedRoutes.getDefaultInstance()
        }
    }

    override suspend fun writeTo(
        t: SavedRoutes,
        output: OutputStream
    ) {
        t.writeTo(output)
    }

}
