package com.bunk.uvindex

import android.location.Location
import com.bunk.uvindex.api.WeatherData
import com.bunk.uvindex.api.WeatherRepository
import com.bunk.uvindex.location.LocationRepository
import com.bunk.uvindex.mapper.getLocation
import com.bunk.uvindex.mapper.toUvEntity
import com.bunk.uvindex.storage.database.UvEntity
import com.bunk.uvindex.storage.UvRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt

class GetCurrentUvIndexUseCase(
	private val weatherRepository: WeatherRepository,
	private val locationRepository: LocationRepository,
	private val uvRepository: UvRepository,
) {

	suspend fun execute(): UvIndex = withContext(Dispatchers.IO) { // TODO does room require going of the main thread?
		// clean up DB, delete past elements
		val now = Instant.now()
		uvRepository.deleteOlderThan(now)

		// find the element that is closest (30min) to now
		var closestToNowUvEntity: UvEntity? = uvRepository.getClosestTo(now)
		Timber.d("min = $closestToNowUvEntity (date = ${toDate(closestToNowUvEntity?.dt)})")

		val location: Location? = locationRepository.getLocation()
		if (didLocationExceedThreshold(location, otherLocation = closestToNowUvEntity?.getLocation())) {
			// if the phone moved too far, delete all entries in DB
			uvRepository.deleteAll()
			closestToNowUvEntity = null
			Timber.d("device move more than $MIN_DISTANCE_IN_METER meter")
		}

		val count: Int = uvRepository.count(now)
		Timber.d("$count entries in DB")

		// if there are less than required elements available make a new request
		if (count <= THRESHOLD_MAKING_NEW_REQUEST) {
			Timber.i("too few elements in DB, make request")

			val weatherData: WeatherData? = fetchWeather(location)
			Timber.d("weatherData: $weatherData")

			// insert response in DB
			val uvEntities: List<UvEntity> =
				weatherData?.hourly?.map { it.toUvEntity(longitude = weatherData.lon, latitude = weatherData.lat) } ?: emptyList()
			uvRepository.insertAll(uvEntities)
		}

		val uvIndex = closestToNowUvEntity?.uvIndex ?: uvRepository.getClosestTo(now)?.uvIndex
		return@withContext UvIndex.from(uvIndex?.roundToInt())
	}

	private fun didLocationExceedThreshold(location: Location?, otherLocation: Location?): Boolean {
		return distanceInMeters(location, otherLocation) > MIN_DISTANCE_IN_METER
	}

	private fun distanceInMeters(location: Location?, otherLocation: Location?): Int {
		return if (location != null && otherLocation != null) {
			location.distanceTo(otherLocation).roundToInt()
		} else -1
	}

	private suspend fun fetchWeather(location: Location?): WeatherData? {
		location ?: return null
		return weatherRepository.getWeather(latitude = location.latitude, longitude = location.longitude)
	}

	private fun toDate(epochSeconds: Long?): String {
		epochSeconds ?: return "null"
		val zoneId: ZoneId = TimeZone.getDefault().toZoneId()
		val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-uuuu hh:mm a")
		return Instant.ofEpochSecond(epochSeconds).atZone(zoneId).format(formatter)
	}

	companion object {
		private const val THRESHOLD_MAKING_NEW_REQUEST = 24
		private const val MIN_DISTANCE_IN_METER = 10_000
	}
}