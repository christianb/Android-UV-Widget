package com.bunk.uvindex.api

import com.bunk.uvindex.BuildConfig
import com.bunk.uvindex.api.result.ApiResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.*
import org.junit.Test

class WeatherRepositoryTest {

	private val openWeatherMapApi: OpenWeatherMapApi = mockk()

	private val classUnderTest = WeatherRepository(openWeatherMapApi)

	@Test
	fun `getWeather should return WeatherData when response is successful`() {
		val expected: WeatherData = mockk()
		coEvery {
			openWeatherMapApi.getWeather(latitude = 12.34, longitude = 56.78, apiKey = BuildConfig.API_KEY)
		} returns ApiResponse.Success(expected)

		val actual = runBlocking {
			classUnderTest.getWeather(latitude = 12.34, longitude = 56.78)
		}

		assertThat(actual).isEqualTo(expected)
	}

	@Test
	fun `getWeather should return null when response fails`() {
		coEvery {
			openWeatherMapApi.getWeather(any(), any(), any())
		} returns ApiResponse.Failure("some-error")

		val actual = runBlocking {
			classUnderTest.getWeather(latitude = 12.34, longitude = 56.78)
		}

		assertThat(actual).isNull()
	}
}