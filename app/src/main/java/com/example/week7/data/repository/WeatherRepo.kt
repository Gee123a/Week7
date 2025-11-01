package com.example.week7.data.repository


import com.example.week7.data.service.WeatherApiService
import com.example.week7.data.dto.WeatherResponse
import com.example.week7.ui.model.weatherModel
import retrofit2.HttpException

class WeatherRepository(private val weatherService: WeatherApiService) {
    private val apiKey = "020c2152ae7c25e3aa8506c3f2052af2"

    suspend fun getWeatherData(cityName: String): weatherModel {
        val response = weatherService.getWeather(cityName, apiKey)
        return mapResponseToModel(response)
    }

    fun getWeatherIconUrl(iconCode: String): String {
        return "https://openweathermap.org/img/wn/$iconCode@2x.png"
    }

    private fun mapResponseToModel(response: WeatherResponse): weatherModel {
        return weatherModel(
            cityName = response.name,
            temperature = response.main.temp,
            weatherIconCode = response.weather.firstOrNull()?.icon ?: "",
            humidity = response.main.humidity,
            windSpeed = response.wind.speed,
            feelsLike = response.main.feels_like,
            rainfallLastHour = 0.0,
            pressure = response.main.pressure,
            cloudsAll = response.clouds.all,
            dateTime = response.dt,
            sunriseTime = response.sys.sunrise,
            sunsetTime = response.sys.sunset,
            isError = false,
            errorMessage = null
        )
    }

    suspend fun searchCities(query: String): List<String> {
        return try {
            val response = weatherService.geocodingSearch(query, 5, apiKey)
            response.map { "${it.name}, ${it.country}" }
        } catch (e: HttpException) {
            if (e.code() == 404) {
                println("DEBUG: No cities found for '$query'")
                emptyList()
            } else {
                throw e
            }
        } catch (e: Exception) {
            println("DEBUG: Exception in searchCities: ${e.message}")
            emptyList()
        }
    }
}