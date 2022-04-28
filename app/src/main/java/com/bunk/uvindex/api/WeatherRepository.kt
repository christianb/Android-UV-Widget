package com.bunk.uvindex.api

class WeatherRepository(
	private val openWeatherMapApi: OpenWeatherMapApi,
) {

	suspend fun getWeather(
		latitude: Double,
		longitude: Double,
	): WeatherData? {
		return openWeatherMapApi.getWeather(latitude = latitude, longitude = longitude).dataOrNull
	}
}