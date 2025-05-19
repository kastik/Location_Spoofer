package com.kastik.locationspoofer.data.api

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import retrofit2.Retrofit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val gson = GsonBuilder()
        .registerTypeAdapterFactory(DebuggingAdapterFactory())
        .setLenient()
        .create()


    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://routes.googleapis.com")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()




    val api: GoogleRoutesApi = retrofit.create(GoogleRoutesApi::class.java)
}

class DebuggingAdapterFactory : TypeAdapterFactory {
    override fun <T> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        val delegate = gson.getDelegateAdapter(this, type)

        return object : TypeAdapter<T>() {
            override fun write(out: JsonWriter?, value: T) {
                delegate.write(out, value)
            }

            override fun read(`in`: JsonReader?): T {
                if (`in` == null) {
                    Log.w("GsonDebug", "JsonReader is null for type ${type.type}")
                    return delegate.read(`in`)
                }

                // Buffer the JSON as a string to log it on failure or success
                val jsonString = try {
                    `in`.peek() // to check if reader is valid
                    val jsonTree = com.google.gson.JsonParser.parseReader(`in`)
                    jsonTree.toString()
                } catch (e: Exception) {
                    Log.e("GsonDebug", "Failed to read JSON input as string for logging", e)
                    null
                }

                // Create a new JsonReader from the jsonString, because the original reader is consumed
                val jsonReaderForDelegate = jsonString?.let { JsonReader(it.reader()) } ?: `in`

                return try {
                    val result = delegate.read(jsonReaderForDelegate)
                    Log.i("GsonDebug", "Successfully parsed ${type.type}: $result")
                    result
                } catch (e: Exception) {
                    Log.e("GsonDebug", "Failed to parse ${type.type}. Raw JSON: $jsonString", e)
                    throw e
                }
            }
        }
    }
}

