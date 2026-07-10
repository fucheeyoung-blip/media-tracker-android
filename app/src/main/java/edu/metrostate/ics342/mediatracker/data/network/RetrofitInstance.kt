package edu.metrostate.ics342.mediatracker.data.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import edu.metrostate.ics342.mediatracker.data.SessionRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

object RetrofitInstance {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults    = true
    }

    private fun loggingInterceptor() = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(ApiConstants.BASE_URL)
        .client(OkHttpClient.Builder().addInterceptor(loggingInterceptor()).build())
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    val userApiService: UserApiService = retrofit.create(UserApiService::class.java)

    fun mediaApiService(sessionRepository: SessionRepository): MediaApiService =
        Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(AuthInterceptor(sessionRepository))
                    .addInterceptor(loggingInterceptor())
                    .build()
            )
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(MediaApiService::class.java)
}