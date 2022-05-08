package com.bunk.uvindex.storage

import com.bunk.uvindex.createUvEntity
import com.bunk.uvindex.storage.database.UvDao
import com.bunk.uvindex.storage.database.UvEntity
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
		val expected = createUvEntity(dt = 1)
		coEvery { dao.getAllUpcoming(any()) } returns listOf(expected, createUvEntity(dt = 2), createUvEntity(dt = 3))

		val actual = runBlocking { classUnderTest.getClosestTo(now) }

		assertThat(actual).isEqualTo(expected)
	}

	@Test
	fun `getClosestTo should subtract 30 minutes from now on call getAllUpcoming`() {
		val now = Instant.now()
		coEvery { dao.getAllUpcoming(any()) } returns emptyList()

		runBlocking { classUnderTest.getClosestTo(now) }

		coVerify { dao.getAllUpcoming(now.epochSecond - Duration.ofMinutes(30).seconds) }
	}

	@Test
	fun `getClosestTo should get null when getAllUpcoming is empty`() {
		coEvery { dao.getAllUpcoming(any()) } returns emptyList()

		val actual = runBlocking { classUnderTest.getClosestTo(Instant.now()) }

		assertThat(actual).isNull()
	}

	@Test
	fun `getMaxWithin24Hours should call dao with correct parameters`() {
		val now = Instant.now()
		val expected = createUvEntity()
		coEvery { dao.getMaxUntil(now.epochSecond, now.plus(Duration.ofHours(24)).epochSecond) } returns expected

		val actual = runBlocking { classUnderTest.getMaxWithin24Hours(now) }

		assertThat(actual).isEqualTo(expected)
	}
}