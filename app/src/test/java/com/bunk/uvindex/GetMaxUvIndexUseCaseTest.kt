package com.bunk.uvindex

import com.bunk.uvindex.storage.UvRepository
import io.mockk.Ordering
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.Instant

class GetMaxUvIndexUseCaseTest {

	private val fetchAndUpdateWeatherDataUseCase: FetchAndUpdateWeatherDataUseCase = mockk(relaxed = true)
	private val uvRepository: UvRepository = mockk(relaxed = true)

	private val classUnderTest = GetMaxUvIndexUseCase(fetchAndUpdateWeatherDataUseCase, uvRepository)

	@Before
	fun setUp() {
		mockkStatic(Instant::class)
	}

	@After
	fun tearDown() {
		unmockkAll()
	}

	@Test
	fun `execute should return max uv index`() {
		val now: Instant = mockk { every { Instant.now() } returns this }
		coEvery { uvRepository.getMaxWithin24Hours(now) } returns createUvEntity(uvIndex = 4.2)

		val actual: UvIndex = runBlocking { classUnderTest.execute() }

		assertThat(actual.value).isEqualTo(4.2)
	}

	@Test
	fun `execute should call fetchAndUpdateWeatherDataUseCase and then getMaxWithin24Hours`() {
		val now: Instant = mockk { every { Instant.now() } returns this }

		runBlocking { classUnderTest.execute() }

		coVerify(ordering = Ordering.ORDERED) {
			fetchAndUpdateWeatherDataUseCase.execute(minRequiredEntriesInDb = 24)
			uvRepository.getMaxWithin24Hours(now)
		}
	}
}