package com.bunk.uvindex.storage.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "UvEntity")
data class UvEntity(
	@PrimaryKey val dt: Long,
	val uvIndex: Double,
	val longitude: Double,
	val latitude: Double
)