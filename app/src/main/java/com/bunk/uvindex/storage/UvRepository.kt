package com.bunk.uvindex.storage

import com.bunk.uvindex.storage.database.UvDao
import com.bunk.uvindex.storage.database.UvEntity
import java.time.Duration

class UvRepository(
	private val dao: UvDao
) {

	suspend fun getAll(nowInSeconds: Long): List<UvEntity> = dao.getAll(nowInSeconds - HALF_AN_HOUR_IN_SECONDS)

	suspend fun insertAll(uvEntities: List<UvEntity>) = dao.insertAll(uvEntities)

	suspend fun deleteOlderThan(nowInSeconds: Long) = dao.delete(nowInSeconds - HALF_AN_HOUR_IN_SECONDS)

	suspend fun count(nowInSeconds: Long): Int = dao.count(nowInSeconds)

	companion object {
		private val HALF_AN_HOUR_IN_SECONDS = Duration.ofMinutes(30).seconds

	}
}