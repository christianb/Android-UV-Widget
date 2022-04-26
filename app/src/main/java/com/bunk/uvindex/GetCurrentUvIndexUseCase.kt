package com.bunk.uvindex

import android.location.Location
import com.bunk.uvindex.api.WeatherData
import com.bunk.uvindex.api.WeatherRepository
import com.bunk.uvindex.location.LocationRepository
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
		// clean up DB
		val now = Instant.now()
		uvRepository.deleteOlderThan(now)

		// count elements in DB
		val count: Int = uvRepository.count(now)
		Timber.d("$count entries in DB")

		if (count <= THRESHOLD_MAKING_NEW_REQUEST) {
			Timber.i("too few elements in DB, make request")

			val weatherData = fetchWeather()
			Timber.d("weatherData: $weatherData")

			// insert response in DB
			val uvEntities: List<UvEntity> = weatherData?.hourly?.map { it.toUvEntity() } ?: emptyList()
			uvRepository.insertAll(uvEntities)
		}

		// get all entries from DB
		val all: List<UvEntity> = uvRepository.getAll(now)
		Timber.d("all = $all")

		val closest: UvEntity? = getClosest(all, now)
		Timber.d("closestToNow = $closest (date: ${toDate(closest?.dt)})")

		return@withContext UvIndex.from(closest?.uvIndex?.roundToInt())

	}

	private suspend fun fetchWeather(): WeatherData? {
		val location: Location = locationRepository.getLocation()?.also {
			Timber.d("location: $it")
		} ?: return null

		return weatherRepository.getWeather(latitude = location.latitude, longitude = location.longitude)
	}

	private fun toDate(epochSeconds: Long?): String {
		epochSeconds ?: return "null"
		val zoneId: ZoneId = TimeZone.getDefault().toZoneId()
		val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-uuuu hh:mm a")
		return Instant.ofEpochSecond(epochSeconds).atZone(zoneId).format(formatter)
	}

	private fun getClosest(all: List<UvEntity>, now: Instant): UvEntity? {
		var closest: UvEntity? = null
		val nowInSeconds = now.epochSecond

		for (element in all) {
			if (closest == null || element.dt - nowInSeconds < closest.dt - nowInSeconds) {
				closest = element
			}
		}

		return closest
	}

	companion object {
		private const val THRESHOLD_MAKING_NEW_REQUEST = 24
	}
}