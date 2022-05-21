package com.bunk.uvindex.api

import com.bunk.uvindex.BuildConfig
import com.bunk.uvindex.api.result.ApiResponse
import com.bunk.uvindex.provider.ConnectivityProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.*
import org.junit.Test

class WeatherRepositoryTest {

	private val openWeatherMapApi: OpenWeatherMapApi = mockk()
	private val connectivityProvider: ConnectivityProvider = mockk()

	private val classUnderTest = WeatherRepository(openWeatherMapApi, connectivityProvider)

	@Test
	fun `getWeather should return null when network is not available`() {
		every { connectivityProvider.isNetworkAvailable() } returns false

		val actual = runBlocking {
			classUnderTest.getWeather(latitude = 1.2, longitude = 2.3)
		}

		assertThat(actual).isNull()
	}

	@Test
	fun `getWeather should return WeatherData when response is successful`() {
		every { connectivityProvider.isNetworkAvailable() } returns true
		val expected: WeatherData = mockk()
		coEvery {
			openWeatherMapApi.getWeather(latitude = 1.2, longitude = 3.4)
		} returns ApiResponse.Success(expected)

		val actual = runBlocking {
			classUnderTest.getWeather(latitude = 1.2, longitude = 3.4)
		}

		assertThat(actual).isEqualTo(expected)
		coVerify { openWeatherMapApi.getWeather(latitude = 1.2, longitude = 3.4) }
	}

	@Test
	fun `getWeather should return null when response fails`() {
		every { connectivityProvider.isNetworkAvailable() } returns true
		coEvery {
			openWeatherMapApi.getWeather(latitude = 1.2, longitude = 3.4)
		} returns ApiResponse.Failure("some-error")

		val actual = runBlocking {
			classUnderTest.getWeather(latitude = 1.2, longitude = 3.4)
		}

		assertThat(actual).isNull()
		coVerify { openWeatherMapApi.getWeather(latitude = 1.2, longitude = 3.4) }
	}
}