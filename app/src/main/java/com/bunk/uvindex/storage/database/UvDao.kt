package com.bunk.uvindex.storage.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UvDao {

	@Query("SELECT * FROM UvEntity WHERE dt >= :nowInSeconds ORDER BY dt ASC")
	suspend fun getAllUpcoming(nowInSeconds: Long): List<UvEntity>

	@Insert
	suspend fun insertAll(uvEntities: List<UvEntity>)

	@Query("DELETE FROM UvEntity WHERE dt < (:nowInSeconds)")
	suspend fun deleteOlderThan(nowInSeconds: Long)

	@Query("DELETE FROM UvEntity")
	suspend fun deleteAll()

	@Query("SELECT COUNT(dt) FROM UvEntity WHERE dt >= :nowInSeconds")
	suspend fun countUpcoming(nowInSeconds: Long): Int
}