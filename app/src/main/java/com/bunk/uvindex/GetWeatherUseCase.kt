package com.bunk.uvindex

import android.location.Location
import com.bunk.uvindex.api.WeatherData
import com.bunk.uvindex.api.WeatherRepository
import com.bunk.uvindex.location.LocationRepository
import timber.log.Timber

class GetWeatherUseCase(
	private val weatherRepository: WeatherRepository,
	private val locationRepository: LocationRepository
) {

	suspend fun execute(): WeatherData? {
		val location: Location? = locationRepository.getLocation()
		Timber.d("location: $location")

		location ?: return null

		return weatherRepository.getWeather(latitude = location.latitude, longitude = location.longitude)
	}
}