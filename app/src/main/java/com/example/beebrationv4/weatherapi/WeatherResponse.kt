package com.example.beebrationv4.weatherapi

data class WeatherResponse(
    val main: MainWeatherData
) {
    data class MainWeatherData(
        val temp: Double,
        val humidity: Int
    )
}

