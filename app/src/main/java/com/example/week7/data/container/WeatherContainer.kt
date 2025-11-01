package com.example.week7.data.container

import com.example.week7.data.repository.WeatherRepository
import com.example.week7.data.service.WeatherApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WeatherContainer {
    private const val BASE_URL = "https://api.openweathermap.org"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val weatherService: WeatherApiService by lazy {
        retrofit.create(WeatherApiService::class.java)
    }

    val weatherRepository: WeatherRepository by lazy {
        WeatherRepository(weatherService)
    }
}