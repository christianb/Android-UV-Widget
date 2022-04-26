package com.bunk.uvindex.storage.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UvEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
	abstract fun uvDao(): UvDao

	companion object {
		fun build(applicationContext: Context): AppDatabase {
			return Room.databaseBuilder(
				applicationContext,
				AppDatabase::class.java,
				"app-database"
			).build()
		}
	}
}