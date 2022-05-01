package com.bunk.uvindex

import android.content.Context
import androidx.core.content.edit

class UvIndexSharedPreferences(
	context: Context
) {

	private val sharedPreferences = context.getSharedPreferences("uv_test", Context.MODE_PRIVATE)

	fun set(value: Int) {
		sharedPreferences.edit {
			putInt(KEY, value)
		}
	}

	fun get(): Int = sharedPreferences.getInt(KEY, -1)

	companion object {
		private const val KEY = "number"
	}
}