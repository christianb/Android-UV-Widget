package com.bunk.uvindex.storage.database

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.Duration
import java.time.Instant

class UvDaoTest {

	private lateinit var classUnderTest: AppDatabase

	@Before
	fun setUp() {
		classUnderTest = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().context,
													  AppDatabase::class.java).build()
	}

	@After
	fun tearDown() {
		classUnderTest.close()
	}

	@Test
	fun getAllUpcoming_should_return_empty_list_when_db_is_empty() {
		val actual: List<UvEntity> = runBlocking {
			classUnderTest.uvDao().getAllUpcoming(Instant.now().epochSecond)
		}

		assertThat(actual).isEmpty()
	}

	@Test
	fun insert_should_insert_entities() {
		val entity = createUvEntity(dt = 1234)

		val actual = runBlocking {
			classUnderTest.uvDao().insert(listOf(entity))
			classUnderTest.uvDao().getAllUpcoming(nowInSeconds = 0)
		}

		assertThat(actual).containsExactly(entity)
	}

	@Test
	fun maxUntil_should_return_single_entity() {
		val now = Instant.now()
		val entity1 = createUvEntity(dt = now.epochSecond, uvIndex = 1.2)

		val actual = runBlocking {
			classUnderTest.uvDao().insert(listOf(entity1))
			classUnderTest.uvDao().getMaxUntil(now.epochSecond, now.plus(Duration.ofHours(24)).epochSecond)
		}

		assertThat(actual).isEqualTo(entity1)
	}

	@Test
	fun maxUntil_should_return_null_when_db_is_empty() {
		val now = Instant.now()

		val actual = runBlocking {
			classUnderTest.uvDao().getMaxUntil(now.epochSecond, now.plus(Duration.ofHours(24)).epochSecond)
		}

		assertThat(actual).isNull()
	}

	@Test
	fun maxUntil_should_return_max_value_from_multiple_entries_in_db() {
		val now = Instant.now()
		val entity1 = createUvEntity(dt = now.epochSecond, uvIndex = 1.2)
		val entity2 = createUvEntity(dt = now.epochSecond+2, uvIndex = 2.4)
		val entity3 = createUvEntity(dt = now.epochSecond+4, uvIndex = 1.8)


		val actual = runBlocking {
			classUnderTest.uvDao().insert(listOf(entity1, entity2, entity3))
			classUnderTest.uvDao().getMaxUntil(now.epochSecond, now.plus(Duration.ofHours(24)).epochSecond)
		}

		assertThat(actual).isEqualTo(entity2)
	}

	@Test
	fun maxUntil_should_return_max_value_that_is_within_24_hours_range() {
		val now = Instant.now()
		val entity1 = createUvEntity(dt = now.epochSecond-1, uvIndex = 9.2)
		val entity2 = createUvEntity(dt = now.epochSecond+2, uvIndex = 2.4)
		val entity3 = createUvEntity(dt = now.epochSecond+4, uvIndex = 3.9)
		val entity4 = createUvEntity(dt = now.plus(Duration.ofHours(24)).epochSecond+1, uvIndex = 9.8)


		val actual = runBlocking {
			classUnderTest.uvDao().insert(listOf(entity1, entity2, entity3, entity4))
			classUnderTest.uvDao().getMaxUntil(now.epochSecond, now.plus(Duration.ofHours(24)).epochSecond)
		}

		assertThat(actual).isEqualTo(entity3)
	}

	@Test
	fun getAllUpcoming_should_only_return_entities_where_dt_is_greater_equals_nowInSeconds() {
		val entity1 = createUvEntity(dt = 123)
		val entity2 = createUvEntity(dt = 99)
		val entity3 = createUvEntity(dt = 100)

		val actual = runBlocking {
			classUnderTest.uvDao().insert(listOf(entity1, entity2, entity3))
			classUnderTest.uvDao().getAllUpcoming(nowInSeconds = 100)
		}

		assertThat(actual).containsExactlyInAnyOrder(entity1, entity3)
	}

	@Test
	fun getAllUpcoming_should_return_entities_ordered_by_dt_ascending() {
		val entity1 = createUvEntity(dt = 123)
		val entity2 = createUvEntity(dt = 99)
		val entity3 = createUvEntity(dt = 2145)
		val entity4 = createUvEntity(dt = 3)

		val actual = runBlocking {
			classUnderTest.uvDao().insert(listOf(entity1, entity2, entity3, entity4))
			classUnderTest.uvDao().getAllUpcoming(nowInSeconds = 2)
		}

		assertThat(actual).containsExactly(entity4, entity2, entity1, entity3)
	}

	@Test
	fun deleteAll_should_delete_all_entities() {
		val entity = createUvEntity(dt = 123)

		val actual = runBlocking {
			classUnderTest.uvDao().insert(listOf(entity))
			classUnderTest.uvDao().deleteAll()
			classUnderTest.uvDao().getAllUpcoming(nowInSeconds = 0)
		}

		assertThat(actual).isEmpty()
	}

	@Test
	fun deleteOlderThan_should_delete_all_entities_where_dt_is_smaller_than_nowInSeconds() {
		val entity1 = createUvEntity(dt = 123)
		val entity2 = createUvEntity(dt = 99)
		val entity3 = createUvEntity(dt = 100)
		val entity4 = createUvEntity(dt = 256)

		val actual = runBlocking {
			classUnderTest.uvDao().insert(listOf(entity1, entity2, entity3, entity4))
			classUnderTest.uvDao().deleteOlderThan(nowInSeconds = 100)
			classUnderTest.uvDao().getAllUpcoming(nowInSeconds = 100)
		}

		assertThat(actual).containsExactly(entity3, entity1, entity4)
	}

	@Test
	fun countUpcoming_should_return_correct_number_of_rows_in_db() {
		val entity1 = createUvEntity(dt = 123)
		val entity2 = createUvEntity(dt = 99)
		val entity3 = createUvEntity(dt = 100)
		val entity4 = createUvEntity(dt = 256)

		val actual = runBlocking {
			classUnderTest.uvDao().insert(listOf(entity1, entity2, entity3, entity4))
			classUnderTest.uvDao().countUpcoming(nowInSeconds = 100)
		}

		assertThat(actual).isEqualTo(3)
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