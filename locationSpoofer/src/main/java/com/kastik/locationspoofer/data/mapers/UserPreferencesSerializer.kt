package com.kastik.locationspoofer.data.mapers

import androidx.datastore.core.Serializer
import com.kastik.locationspoofer.UserPreferences
import java.io.InputStream
import java.io.OutputStream

object UserPreferencesSerializer : Serializer<UserPreferences> {
    override val defaultValue: UserPreferences = UserPreferences.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UserPreferences {
        return try {
            UserPreferences.parseFrom(input)
        }catch (_ : Exception){
            UserPreferences.getDefaultInstance()
        }
    }

    override suspend fun writeTo(
        t: UserPreferences,
        output: OutputStream
    ) {
        t.writeTo(output)
    }

}
