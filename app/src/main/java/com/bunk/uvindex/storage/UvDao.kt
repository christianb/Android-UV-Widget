package com.bunk.uvindex.storage

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UvDao {

	@Query("SELECT * FROM UvEntity WHERE dt > :nowInSeconds")
	suspend fun getAll(nowInSeconds: Long): List<UvEntity>

	@Insert
	suspend fun insertAll(uvEntities: List<UvEntity>)

	@Query("DELETE FROM UvEntity WHERE dt < (:nowInSeconds - 1800)")
	suspend fun delete(nowInSeconds: Long)

	@Query("SELECT COUNT(dt) FROM UvEntity WHERE dt > :nowInSeconds")
	suspend fun count(nowInSeconds: Long): Int
}