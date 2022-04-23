package com.bunk.uvindex.api

import com.bunk.uvindex.Cache
import timber.log.Timber

class WeatherRepository(
	private val openWeatherMapApi: OpenWeatherMapApi,
) {

	private val cache: Cache<WeatherData> = Cache()

	suspend fun getWeather(
		latitude: String,
		longitude: String,
		apiKey: String,
	): WeatherData? {
		Timber.d("getWeather")
		if (cache.isStale()) {
			val weatherData: WeatherData? = openWeatherMapApi.getWeather(latitude = latitude, longitude = longitude, apiKey).dataOrNull
			Timber.d("cache stale")
			Timber.d("weatherData: $weatherData")
			if (weatherData != null) cache.value = weatherData
		} else {
			Timber.d("cache not stale")
		}

		return cache.value
	}
}