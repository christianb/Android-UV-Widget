package com.bunk.uvindex.storage

import com.bunk.uvindex.UvIndex
import com.bunk.uvindex.createUvEntity
import com.bunk.uvindex.storage.database.UvDao
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.*
import org.junit.Test
import java.time.Duration

import java.time.Instant

class UvRepositoryTest {

	private val dao: UvDao = mockk(relaxed = true)

	private val classUnderTest = UvRepository(dao)

	@Test
	fun insert() {
		val uvEntities = listOf(createUvEntity())

		runBlocking { classUnderTest.insert(uvEntities) }

		coVerify { dao.insert(uvEntities) }
	}

	@Test
	fun deleteAll() {
		runBlocking { classUnderTest.deleteAll() }

		coVerify { dao.deleteAll() }
	}

	@Test
	fun count() {
		val now = Instant.now()
		coEvery { dao.countUpcoming(any()) } returns 42

		val actual = runBlocking {
			classUnderTest.numberOfElementsAfter(now)
		}

		assertThat(actual).isEqualTo(42)
		coVerify { dao.countUpcoming(now.epochSecond) }
	}

	@Test
	fun `getClosestTo should get first of getAllUpcoming`() {
		val now = Instant.now()
		coEvery { dao.getAllUpcoming(any()) } returns listOf(
			createUvEntity(dt = 1, uvIndex = 2.5),
			createUvEntity(dt = 2, uvIndex = 5.1),
			createUvEntity(dt = 3, uvIndex = 7.2)
		)

		val actual: Double = runBlocking { classUnderTest.getClosestTo(now).value }

		assertThat(actual).isEqualTo(2.5)
	}

	@Test
	fun `getClosestTo should subtract 30 minutes from now on call getAllUpcoming`() {
		val now = Instant.now()
		coEvery { dao.getAllUpcoming(any()) } returns emptyList()

		runBlocking { classUnderTest.getClosestTo(now) }

		coVerify { dao.getAllUpcoming(now.epochSecond - Duration.ofMinutes(30).seconds) }
	}

	@Test
	fun `getClosestTo should get UvIndex Unknown when getAllUpcoming is empty`() {
		coEvery { dao.getAllUpcoming(any()) } returns emptyList()

		val actual = runBlocking { classUnderTest.getClosestTo(Instant.now()) }

		assertThat(actual).isEqualTo(UvIndex.Unknown)
	}

	@Test
	fun `getMaxWithin24Hours should call dao with correct parameters`() {
		val now = Instant.now()
		coEvery { dao.getMaxUntil(now.epochSecond, now.plus(Duration.ofHours(24)).epochSecond) } returns createUvEntity(uvIndex = 4.3)

		val actual: Double = runBlocking { classUnderTest.getMaxNext24Hours(now).value }

		assertThat(actual).isEqualTo(4.3)
	}
}