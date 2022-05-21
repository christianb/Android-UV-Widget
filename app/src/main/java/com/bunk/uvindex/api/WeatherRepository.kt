package com.bunk.uvindex.api

import com.bunk.uvindex.provider.ConnectivityProvider

class WeatherRepository(
	private val openWeatherMapApi: OpenWeatherMapApi,
	private val connectivityProvider: ConnectivityProvider,
) {

	suspend fun getWeather(
		latitude: Double,
		longitude: Double,
	): WeatherData? {
		if (!connectivityProvider.isNetworkAvailable()) return null
		return openWeatherMapApi.getWeather(latitude = latitude, longitude = longitude).dataOrNull
	}
}