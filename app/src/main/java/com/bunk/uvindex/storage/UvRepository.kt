package com.bunk.uvindex.storage

import com.bunk.uvindex.UvIndex
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

	suspend fun getClosestTo(instant: Instant): UvIndex {
		val uvIndex: Double? = dao.getAllUpcoming(instant.epochSecond - HALF_AN_HOUR_IN_SECONDS).firstOrNull()?.uvIndex
		return UvIndex.from(uvIndex)
	}

	suspend fun getMaxNext24Hours(now: Instant): UvIndex {
		val uvIndex: Double? = dao.getMaxUntil(now.epochSecond, now.plus(Duration.ofHours(24)).epochSecond)?.uvIndex
		return UvIndex.from(uvIndex)
	}

	companion object {
		private val HALF_AN_HOUR_IN_SECONDS = Duration.ofMinutes(30).seconds

	}
}