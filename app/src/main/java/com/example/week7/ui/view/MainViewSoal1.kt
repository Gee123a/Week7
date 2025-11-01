package com.example.week7.ui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.week7.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.week7.ui.viewmodel.viewmodelSoal1
import kotlin.collections.get
import kotlin.text.compareTo
import kotlin.text.get

@Composable
fun MainViewSoal1(
    viewModel: viewmodelSoal1 = viewModel()
) {
    var textValue by remember { mutableStateOf("") }
    val weatherState by viewModel.weather.collectAsState()
    val weatherIconUrl by viewModel.weatherIconUrl.collectAsState()
    val citySuggestions by viewModel.citySuggestions.collectAsState()
    var showSuggestions by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.weather___home_2),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = textValue,
                        onValueChange = {
                            textValue = it
                            viewModel.searchCities(it)
                            showSuggestions = it.isNotEmpty()
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Icon",
                                tint = Color.White
                            )
                        },
                        label = {
                            Text(
                                text = "Enter city name",
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        },
                        colors = androidx.compose.material3.TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.White,
                            unfocusedIndicatorColor = Color.White.copy(alpha = 0.5f),
                            cursorColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = {
                            if (textValue.isNotBlank()) {
                                viewModel.loadWeather(textValue)
                                showSuggestions = false
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Icon"
                        )
                    }
                }

                if (showSuggestions && citySuggestions.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.9f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        LazyColumn {
                            items(citySuggestions.size) { index ->
                                val city = citySuggestions[index]
                                Text(
                                    text = city,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            textValue = city.split(",")[0]
                                            viewModel.loadWeather(textValue)
                                            showSuggestions = false
                                        }
                                        .padding(16.dp),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                if (index < citySuggestions.size - 1) {
                                    HorizontalDivider()
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (weatherState.isError) {
                ErrorView(errorMessage = weatherState.errorMessage)
            } else if (weatherState.cityName.isNotEmpty()) {
                // Weather icon and temperature section
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    weatherIconUrl?.let { iconUrl ->
                        AsyncImage(
                            model = iconUrl,
                            contentDescription = "Weather Icon",
                            modifier = Modifier.size(100.dp)
                        )
                    }

                    Text(
                        text = "${weatherState.temperature ?: 0}°",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        text = weatherState.cityName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        WeatherInfoCard(
                            iconRes = R.drawable.icon_humidity,
                            label = "Humidity",
                            value = "${weatherState.humidity ?: 0}%"
                        )
                    }
                    item {
                        WeatherInfoCard(
                            iconRes = R.drawable.icon_wind,
                            label = "Wind",
                            value = "${weatherState.windSpeed ?: 0} km/h"
                        )
                    }
                    item {
                        WeatherInfoCard(
                            iconRes = R.drawable.icon_feels_like,
                            label = "Feels Like",
                            value = "${weatherState.feelsLike ?: 0}°"
                        )
                    }
                    item {
                        WeatherInfoCard(
                            iconRes = R.drawable.vector_2,
                            label = "Rainfall",
                            value = "${weatherState.rainfallLastHour ?: 0} mm"
                        )
                    }
                    item {
                        WeatherInfoCard(
                            iconRes = R.drawable.devices,
                            label = "Pressure",
                            value = "${weatherState.pressure ?: 0} hPa"
                        )
                    }
                    item {
                        WeatherInfoCard(
                            iconRes = R.drawable.cloud,
                            label = "Clouds",
                            value = "${weatherState.cloudsAll ?: 0}%"
                        )
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PreviewMainViewSoal1() {
    MainViewSoal1()
}