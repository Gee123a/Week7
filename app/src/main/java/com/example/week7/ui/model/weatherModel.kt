package com.example.week7.ui.model

data class weatherModel(
    val cityName: String = "",
    val dateTime: Int = 0,
    val weatherIconCode: String = "",
    val weatherCondition: String = "",
    val temperature: Double = 0.0,
    val humidity: Int = 0,
    val windSpeed: Double = 0.0,
    val feelsLike: Double = 0.0,
    val rainfallLastHour: Double? = null,
    val pressure: Int = 0,
    val cloudsAll: Int = 0,
    val sunriseTime: Int = 0,
    val sunsetTime: Int = 0,
    val isError: Boolean = false,
    val errorMessage: String? = null
)
