package com.bunk.uvindex.api

import com.bunk.uvindex.Cache
import timber.log.Timber

class WeatherRepository(
	private val openWeatherMapApi: OpenWeatherMapApi,
) {

	// TODO does not take location into consideration...
	private val cache: Cache<WeatherData> = Cache()

	fun last(): Cache<WeatherData> = cache

	suspend fun getWeather(
		latitude: Double,
		longitude: Double,
	): WeatherData? {
		Timber.d("getWeather")
		if (cache.isStale()) {
			val weatherData: WeatherData? = openWeatherMapApi.getWeather(latitude = latitude, longitude = longitude).dataOrNull
			Timber.d("cache stale")
			Timber.d("weatherData: $weatherData")
			if (weatherData != null) cache.value = weatherData
		} else {
			Timber.d("cache not stale")
		}

		return cache.value
	}
}