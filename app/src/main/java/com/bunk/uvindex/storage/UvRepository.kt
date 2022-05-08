package com.bunk.uvindex.storage

import com.bunk.uvindex.storage.database.UvDao
import com.bunk.uvindex.storage.database.UvEntity
import java.time.Duration
import java.time.Instant

class UvRepository(
	private val dao: UvDao
) {

	suspend fun insert(uvEntities: List<UvEntity>) {
		dao.insert(uvEntities)
	}

	suspend fun deleteAll() {
		dao.deleteAll()
	}

	suspend fun numberOfElementsAfter(now: Instant): Int = dao.countUpcoming(now.epochSecond)

	suspend fun getClosestTo(instant: Instant): UvEntity? {
		return dao.getAllUpcoming(instant.epochSecond - HALF_AN_HOUR_IN_SECONDS).firstOrNull()
	}

	suspend fun getMaxWithin24Hours(now: Instant): UvEntity? {
		return dao.getMaxUntil(now.epochSecond, now.plus(Duration.ofHours(24)).epochSecond)
	}

	companion object {
		private val HALF_AN_HOUR_IN_SECONDS = Duration.ofMinutes(30).seconds

	}
}