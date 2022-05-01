package com.bunk.uvindex.storage

import com.bunk.uvindex.storage.database.UvDao
import com.bunk.uvindex.storage.database.UvEntity
import java.time.Duration
import java.time.Instant

class UvRepository(
	private val dao: UvDao
) {

	suspend fun insertAll(uvEntities: List<UvEntity>) {
		dao.deleteAll()
		dao.insertAll(uvEntities)
	}

	suspend fun countUpcoming(now: Instant): Int = dao.countUpcoming(now.epochSecond)

	suspend fun getClosestTo(instant: Instant): UvEntity? {
		return dao.getAllUpcoming(instant.epochSecond - HALF_AN_HOUR_IN_SECONDS).firstOrNull()
	}

	companion object {
		private val HALF_AN_HOUR_IN_SECONDS = Duration.ofMinutes(30).seconds

	}
}