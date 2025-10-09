package com.kastik.locationspoofer.data.datastore

import androidx.datastore.core.Serializer
import com.kastik.locationspoofer.SavedPlaces
import java.io.InputStream
import java.io.OutputStream

object PlacesSerializer : Serializer<SavedPlaces> {
    override val defaultValue: SavedPlaces = SavedPlaces.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): SavedPlaces {
        return try {
            SavedPlaces.parseFrom(input)
        }catch (_: Exception){
            SavedPlaces.getDefaultInstance()
        }
    }

    override suspend fun writeTo(
        t: SavedPlaces,
        output: OutputStream
    ) {
        t.writeTo(output)
    }

}
