package com.bunk.uvindex.storage

import com.bunk.uvindex.storage.database.UvDao
import com.bunk.uvindex.storage.database.UvEntity
import java.time.Duration
import java.time.Instant

class UvRepository(
	private val dao: UvDao
) {

	suspend fun getAll(now: Instant): List<UvEntity> = dao.getAll(now.epochSecond - HALF_AN_HOUR_IN_SECONDS)

	suspend fun insertAll(uvEntities: List<UvEntity>) = dao.insertAll(uvEntities)

	suspend fun deleteOlderThan(now: Instant) = dao.delete(now.epochSecond - HALF_AN_HOUR_IN_SECONDS)

	suspend fun deleteAll() = dao.deleteAll()

	suspend fun count(now: Instant): Int = dao.count(now.epochSecond)

	suspend fun getClosestTo(instant: Instant): UvEntity? = getAll(instant).firstOrNull()

	companion object {
		private val HALF_AN_HOUR_IN_SECONDS = Duration.ofMinutes(30).seconds

	}
}