package com.bunk.uvindex.storage.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UvDao {

	@Query("SELECT * FROM UvEntity WHERE dt >= :nowInSeconds ORDER BY dt ASC")
	suspend fun getAllUpcoming(nowInSeconds: Long): List<UvEntity>

	@Query("SELECT * FROM UvEntity WHERE dt >= :nowInSeconds AND dt < :untilInSeconds ORDER BY uvIndex DESC LIMIT 1")
	suspend fun getMaxUntil(nowInSeconds: Long, untilInSeconds: Long): UvEntity?

	@Insert
	suspend fun insert(uvEntities: List<UvEntity>)

	@Query("DELETE FROM UvEntity WHERE dt < (:nowInSeconds)")
	suspend fun deleteOlderThan(nowInSeconds: Long)

	@Query("DELETE FROM UvEntity")
	suspend fun deleteAll()

	@Query("SELECT COUNT(dt) FROM UvEntity WHERE dt >= :nowInSeconds")
	suspend fun countUpcoming(nowInSeconds: Long): Int
}