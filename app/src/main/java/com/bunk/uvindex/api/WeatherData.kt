package com.bunk.uvindex.api

data class WeatherData(
	val lat: Double,
	val lon: Double,
	val current: Current,
	val hourly: List<Hourly>,
) {

	data class Current(
		val uvi: Double,
	)

	data class Hourly(
		val dt: Long,
		val uvi: Double
	)
}




