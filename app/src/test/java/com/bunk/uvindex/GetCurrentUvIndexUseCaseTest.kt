package com.bunk.uvindex

import android.location.Location
import com.bunk.uvindex.api.WeatherData
import com.bunk.uvindex.api.WeatherRepository
import com.bunk.uvindex.location.LocationRepository
import com.bunk.uvindex.storage.UvRepository
import com.bunk.uvindex.storage.database.UvEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.junit.After
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import java.time.Instant

class GetCurrentUvIndexUseCaseTest {

	private val weatherRepository: WeatherRepository = mockk()
	private val locationRepository: LocationRepository = mockk()
	private val uvRepository: UvRepository = mockk(relaxed = true)

	private val classUnderTest = GetCurrentUvIndexUseCase(weatherRepository, locationRepository, uvRepository)

	@Before
	fun setUp() {
		mockkStatic(Instant::class)
	}

	@After
	fun tearDown() {
		unmockkAll()
	}

	@Test
	fun `execute should get closest to now`() {
		val now: Instant = mockk {
			every { Instant.now() } returns this
		}
		every { locationRepository.getLocation() } returns null
		coEvery { uvRepository.countUpcoming(any()) } returns 0
		coEvery { uvRepository.getClosestTo(now) } returns createUvEntity(uvIndex = 7.0)

		val actual = runBlocking { classUnderTest.execute() }

		assertThat(actual).isEqualTo(UvIndex.from(7))
	}

	@Test
	fun `execute should fetch and update weather when location is not null and less than min elements in DB`() {
		mockk<Instant> {
			every { Instant.now() } returns this
		}
		val latitude = 23.4
		val longitude = 5.6
		val location: Location = mockk()
		every { location.longitude } returns longitude
		every { location.latitude } returns latitude

		every { locationRepository.getLocation() } returns location
		coEvery { uvRepository.countUpcoming(any()) } returns 0
		val weatherData = WeatherData(lat = 23.4, lon = 5.6, hourly = listOf(WeatherData.Hourly(dt = 4, uvi = 8.9)))
		coEvery { weatherRepository.getWeather(latitude = latitude, longitude = longitude) } returns weatherData

		runBlocking { classUnderTest.execute() }

		coVerify {
			uvRepository.insertAll(match {
				val item = UvEntity(dt = 4, uvIndex = 8.9, longitude = 5.6, latitude = 23.4)
				it.contains(item)
			})
		}
	}

	@Test
	fun `execute should not fetch weather when location is null`() {
		val now: Instant = mockk { every { Instant.now() } returns this }
		every { locationRepository.getLocation() } returns null
		coEvery { uvRepository.countUpcoming(any()) } returns 0
		coEvery { uvRepository.getClosestTo(now) } returns createUvEntity()

		runBlocking { classUnderTest.execute() }

		coVerify(exactly = 0) { weatherRepository.getWeather(any(), any()) }
	}

	@Test
	fun `execute should not fetch weather when count is greater threshold`() {
		val now: Instant = mockk { every { Instant.now() } returns this }
		every { locationRepository.getLocation() } returns mockk()
		coEvery { uvRepository.countUpcoming(any()) } returns 1000
		coEvery { uvRepository.getClosestTo(now) } returns createUvEntity()

		runBlocking { classUnderTest.execute() }

		coVerify(exactly = 0) { weatherRepository.getWeather(any(), any()) }
	}

	private fun createUvEntity(
		dt: Long = 0,
		uvIndex: Double = 0.0,
		longitude: Double = 0.0,
		latitude: Double = 0.0,
	): UvEntity {
		return UvEntity(dt = dt, uvIndex = uvIndex, longitude = longitude, latitude = latitude)
	}
}