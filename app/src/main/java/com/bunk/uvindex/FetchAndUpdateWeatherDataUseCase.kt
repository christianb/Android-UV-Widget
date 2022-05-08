package com.bunk.uvindex

import android.location.Location
import com.bunk.uvindex.api.WeatherRepository
import com.bunk.uvindex.location.LocationRepository
import com.bunk.uvindex.mapper.toUvEntity
import com.bunk.uvindex.storage.UvRepository
import com.bunk.uvindex.storage.database.UvEntity
import java.time.Instant

class FetchAndUpdateWeatherDataUseCase(
	private val uvRepository: UvRepository,
	private val locationRepository: LocationRepository,
	private val weatherRepository: WeatherRepository,
) {

	suspend fun execute(minRequiredEntriesInDb: Int) {
		val location: Location = locationRepository.getLocation() ?: return

		val numberOfFutureElements: Int = uvRepository.numberOfElementsAfter(now = Instant.now())

		if (numberOfFutureElements < minRequiredEntriesInDb) fetchAndUpdateWeather(location)
	}

	private suspend fun fetchAndUpdateWeather(location: Location) {
		weatherRepository.getWeather(latitude = location.latitude, longitude = location.longitude)?.also { weatherData ->
			val longitude = weatherData.lon
			val latitude = weatherData.lat
			val uvEntities: List<UvEntity> = weatherData.hourly.map { it.toUvEntity(longitude = longitude, latitude = latitude) }
			uvRepository.deleteAll()
			uvRepository.insert(uvEntities)
		}
	}
}