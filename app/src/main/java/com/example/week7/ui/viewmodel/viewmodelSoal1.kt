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
import kotlinx.coroutines.flow.asStateFlow

open class viewmodelSoal1 : ViewModel() {
    private val _citySuggestions = MutableStateFlow<List<String>>(emptyList())
    val citySuggestions: StateFlow<List<String>> = _citySuggestions
    protected val _weather = MutableStateFlow(weatherModel())

    val weather: StateFlow<weatherModel> = _weather.asStateFlow()

    fun searchCities(query: String) {
        viewModelScope.launch {
            if (query.length >= 2) {
                try {
                    println("DEBUG: Making API call for query='$query'")
                    val suggestions = WeatherContainer.weatherRepository.searchCities(query)
                    println("DEBUG: API returned ${suggestions.size} results: $suggestions")
                    _citySuggestions.value = suggestions
                } catch (e: Exception) {
                    println("DEBUG: searchCities ERROR - ${e.javaClass.simpleName}: ${e.message}")
                    e.printStackTrace()
                    _citySuggestions.value = emptyList()
                }
            } else {
                println("DEBUG: Query too short (${query.length} chars)")
                _citySuggestions.value = emptyList()
            }
        }
    }

    protected val _weatherIconUrl = MutableStateFlow<String?>(null)
    val weatherIconUrl: StateFlow<String?> = _weatherIconUrl.asStateFlow()

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
                    errorMessage = e.message ?: "Unknown error occurred"
                )
                _weatherIconUrl.value = null
            }
        }
    }
}