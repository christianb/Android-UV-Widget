package com.bunk.uvindex.api

data class WeatherData (
	val lat: Double,
	val lon: Double,
	val current: Current
)

data class Current (
	val uvi: Double
)
