package com.bunk.uvindex.api

import timber.log.Timber

class WeatherRepository(
	private val openWeatherMapApi: OpenWeatherMapApi,
) {

	suspend fun getWeather(
		latitude: Double,
		longitude: Double,
	): WeatherData? {
		Timber.d("getWeather")
		return openWeatherMapApi.getWeather(latitude = latitude, longitude = longitude).dataOrNull?.also {
			Timber.d("weather: $it")
		}
	}
}