package com.bunk.uvindex.storage

import com.bunk.uvindex.UvIndex
import com.bunk.uvindex.storage.database.UvDao
import com.bunk.uvindex.storage.database.UvEntity
import java.time.Duration
import java.time.Instant

class UvRepository(
	private val dao: UvDao,
) {

	suspend fun insert(uvEntities: List<UvEntity>) {
		dao.insert(uvEntities)
	}

	suspend fun deleteAll() {
		dao.deleteAll()
	}

	suspend fun numberOfElementsAfter(instant: Instant): Int = dao.countUpcoming(instant.epochSecond)

	suspend fun getNow(): UvIndex {
		val uvIndex: Double? = dao.getAllAfter(Instant.now().epochSecond - HALF_AN_HOUR_IN_SECONDS).firstOrNull()?.uvIndex
		return UvIndex.from(uvIndex)
	}

	suspend fun getMaxNext24Hours(): UvIndex {
		val now = Instant.now()
		val uvEntity = dao.getMaxUntil(
			startInSeconds = now.epochSecond - HALF_AN_HOUR_IN_SECONDS,
			untilInSeconds = now.plus(Duration.ofHours(24)).epochSecond
		)
		return UvIndex.from(uvEntity?.uvIndex)
	}

	companion object {
		val HALF_AN_HOUR_IN_SECONDS = Duration.ofMinutes(30).seconds
	}
}