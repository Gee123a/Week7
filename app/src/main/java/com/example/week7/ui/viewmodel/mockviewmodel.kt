package com.example.week7.ui.viewmodel

import com.example.week7.ui.model.weatherModel
import kotlinx.coroutines.flow.MutableStateFlow

class MockViewModelSoal1 : viewmodelSoal1() {
    init {
        _weather.value = weatherModel(
            cityName = "London",
            temperature = 15.0,
            humidity = 72,
            windSpeed = 12.0,
            feelsLike = 13.0,
            weatherCondition = "Rain",
            rainfallLastHour = 2.5,
            pressure = 1013,
            cloudsAll = 65,
            isError = false,
            errorMessage = null,
            weatherIconCode = "02d",
            dateTime = (System.currentTimeMillis() / 1000).toInt(),
            sunriseTime = (System.currentTimeMillis() / 1000).toInt(),
            sunsetTime = (System.currentTimeMillis() / 1000).toInt()
        )
        _weatherIconUrl.value = "https://openweathermap.org/img/wn/02d@4x.png"
    }
}
