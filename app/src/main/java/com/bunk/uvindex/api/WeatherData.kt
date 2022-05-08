package com.bunk.uvindex.api

// TODO write long parameter names
data class WeatherData(
	val lat: Double,
	val lon: Double,
	val hourly: List<Hourly>,
) {

	data class Hourly(
		val dt: Long,
		val uvi: Double
	)
}




