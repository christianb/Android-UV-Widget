package com.bunk.uvindex

import android.location.Location
import com.bunk.uvindex.api.WeatherData
import com.bunk.uvindex.api.WeatherRepository
import com.bunk.uvindex.location.LocationRepository
import com.bunk.uvindex.storage.UvRepository
import com.bunk.uvindex.storage.database.UvEntity
import io.mockk.Ordering
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.Instant

class FetchAndUpdateWeatherDataUseCaseTest {

	private val uvRepository: UvRepository = mockk(relaxed = true)
	private val locationRepository: LocationRepository = mockk()
	private val weatherRepository: WeatherRepository = mockk()

	private val classUnderTest = FetchAndUpdateWeatherDataUseCase(uvRepository, locationRepository, weatherRepository)

	@Before
	fun setUp() {
		mockkStatic(Instant::class)
	}

	@After
	fun tearDown() {
		unmockkAll()
	}

	@Test
	fun `execute should fetch and update weather data`() {
		mockk<Instant> { every { Instant.now() } returns this }
		val latitude = 4.5
		val longitude = 2.3
		val location: Location = mockkLocation(latitude = latitude, longitude = longitude)
		every { locationRepository.getLocation() } returns location
		coEvery { uvRepository.numberOfElementsAfter(any()) } returns 0
		val weatherData = WeatherData(lat = latitude, lon = longitude, hourly = listOf(WeatherData.Hourly(dt = 123, uvi = 6.7)))
		coEvery { weatherRepository.getWeather(latitude = latitude, longitude = longitude) } returns weatherData

		runBlocking { classUnderTest.execute(minRequiredEntriesInDb = 1) }

		coVerify(ordering = Ordering.ORDERED) {
			uvRepository.deleteAll()
			uvRepository.insert(
				match {
					it.size == 1
							&& it.contains(UvEntity(dt = 123, uvIndex = 6.7, longitude = longitude, latitude = latitude))
				}
			)
		}
	}

	@Test
	fun `execute should not fetch weather data when location is null`() {
		mockk<Instant> { every { Instant.now() } returns this }
		every { locationRepository.getLocation() } returns null
		coEvery { uvRepository.numberOfElementsAfter(any()) } returns 0

		runBlocking { classUnderTest.execute(minRequiredEntriesInDb = 1) }

		coVerify(exactly = 0) { weatherRepository.getWeather(any(), any()) }
	}

	@Test
	fun `execute should not fetch weather data when numberOfFutureElements is equal to minRequiredEntriesInDb`() {
		mockk<Instant> { every { Instant.now() } returns this }
		every { locationRepository.getLocation() } returns mockkLocation()
		coEvery { uvRepository.numberOfElementsAfter(any()) } returns 1

		runBlocking { classUnderTest.execute(minRequiredEntriesInDb = 1) }

		coVerify(exactly = 0) { weatherRepository.getWeather(any(), any()) }
	}

	@Test
	fun `execute should not fetch weather data when numberOfFutureElements is greater than minRequiredEntriesInDb`() {
		mockk<Instant> { every { Instant.now() } returns this }
		every { locationRepository.getLocation() } returns mockkLocation()
		coEvery { uvRepository.numberOfElementsAfter(any()) } returns 2

		runBlocking { classUnderTest.execute(minRequiredEntriesInDb = 1) }

		coVerify(exactly = 0) { weatherRepository.getWeather(any(), any()) }
	}
}