package com.bunk.uvindex.storage.database

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.*
import org.junit.After
import org.junit.Before
import org.junit.Test
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
			classUnderTest.uvDao().insertAll(listOf(entity))
			classUnderTest.uvDao().getAllUpcoming(nowInSeconds = 0)
		}

		assertThat(actual).containsExactly(entity)
	}

	@Test
	fun getAllUpcoming_should_only_return_entities_where_dt_is_greater_equals_nowInSeconds() {
		val entity1 = createUvEntity(dt = 123)
		val entity2 = createUvEntity(dt = 99)
		val entity3 = createUvEntity(dt = 100)

		val actual = runBlocking {
			classUnderTest.uvDao().insertAll(listOf(entity1, entity2, entity3))
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
			classUnderTest.uvDao().insertAll(listOf(entity1, entity2, entity3, entity4))
			classUnderTest.uvDao().getAllUpcoming(nowInSeconds = 2)
		}

		assertThat(actual).containsExactly(entity4, entity2, entity1, entity3)
	}

	@Test
	fun deleteAll_should_delete_all_entities() {
		val entity = createUvEntity(dt = 123)

		val actual = runBlocking {
			classUnderTest.uvDao().insertAll(listOf(entity))
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
			classUnderTest.uvDao().insertAll(listOf(entity1, entity2, entity3, entity4))
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
			classUnderTest.uvDao().insertAll(listOf(entity1, entity2, entity3, entity4))
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