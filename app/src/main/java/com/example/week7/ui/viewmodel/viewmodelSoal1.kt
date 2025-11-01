package com.example.week7.ui.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.week7.ui.model.weatherModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.week7.R
import com.example.week7.data.container.WeatherContainer

class viewmodelSoal1 : ViewModel() {
    private val _citySuggestions = MutableStateFlow<List<String>>(emptyList())
    val citySuggestions: StateFlow<List<String>> = _citySuggestions
    private val _weather = MutableStateFlow(weatherModel())

    val weather: StateFlow<weatherModel> = _weather
    fun searchCities(query: String) {
        viewModelScope.launch {
            if (query.length >= 2) {
                _citySuggestions.value = WeatherContainer.weatherRepository.searchCities(query)
            } else {
                _citySuggestions.value = emptyList()
            }
        }
    }

    private val _weatherIconUrl = MutableStateFlow<String?>(null)
    val weatherIconUrl: StateFlow<String?> = _weatherIconUrl

    val tanggalBerapa = weather.map {
        val date = Date(it.dateTime * 1000L)
        SimpleDateFormat("MMMM dd", Locale("id")).format(date)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, "")

    val jamBerapa = weather.map {
        val date = Date(it.dateTime * 1000L)
        SimpleDateFormat("HH:mm a", Locale("id")).format(date)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, "")

    val sunrieTime = weather.map {
        SimpleDateFormat("HH:mm a", Locale("id")).format(Date(it.sunriseTime * 1000L))
    }.stateIn(viewModelScope, SharingStarted.Eagerly, "")

    val sunsetTime = weather.map {
        SimpleDateFormat("HH:mm a", Locale("id")).format(Date(it.sunsetTime * 1000L))
    }.stateIn(viewModelScope, SharingStarted.Eagerly, "")

    val listWeatherInfo = weather.map {
        listOf(
            Triple("HUMIDITY", "${it.humidity ?: 0}%", R.drawable.icon_humidity),
            Triple("WIND", "${it.windSpeed ?: 0}km/h", R.drawable.icon_wind),
            Triple("FEELS LIKE", "${it.feelsLike ?: 0}°", R.drawable.icon_feels_like),
            Triple("RAIN FALL", "${it.rainfallLastHour ?: 0} mm", R.drawable.vector_2),
            Triple("PRESSURE", "${it.pressure ?: 0}hPa", R.drawable.devices),
            Triple("CLOUDS", "${it.cloudsAll ?: 0}%", R.drawable.cloud)
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val listSunInfo = combine(sunrieTime, sunsetTime) { sunrise, sunset ->
        listOf(
            Triple("SUNRISE", sunrise, R.drawable.vector),
            Triple("SUNSET", sunset, R.drawable.vector_21png)
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun loadWeather(cityName: String) {
        viewModelScope.launch {
            _weather.value = _weather.value.copy(
                isError = false,
                errorMessage = null
            )

            try {
                val result = WeatherContainer.weatherRepository.getWeatherData(cityName)

                _weather.value = result.copy(
                    isError = false,
                    errorMessage = null
                )

                _weatherIconUrl.value = WeatherContainer.weatherRepository.getWeatherIconUrl(
                    result.weatherIconCode
                )

            } catch (e: Exception) {
                _weather.value = _weather.value.copy(
                    isError = true,
                    errorMessage = "HTTP 404 Not Found"
                )
                _weatherIconUrl.value = null
            }
        }
    }
}