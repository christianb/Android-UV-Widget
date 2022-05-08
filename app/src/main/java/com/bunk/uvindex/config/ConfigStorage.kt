package com.bunk.uvindex.config

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class ConfigStorage(
	applicationContext: Context
) {
	private val sharedPreferences: SharedPreferences = applicationContext.getSharedPreferences("config_storage", Context.MODE_PRIVATE)

	fun store(widgetDisplayConfig: WidgetDisplayConfig) {
		sharedPreferences.edit {
			putString(KEY_CONFIG, widgetDisplayConfig.name)
		}
	}

	fun read(): WidgetDisplayConfig? {
		val name = sharedPreferences.getString(KEY_CONFIG, null) ?: return null
		return WidgetDisplayConfig.valueOf(name)
	}

	companion object {
		private const val KEY_CONFIG = "key_widget_display_config"
	}
}