package com.bunk.uvindex.storage

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "UvEntity")
data class UvEntity(
	@PrimaryKey val dt: Long,
	val uvIndex: Double,
)