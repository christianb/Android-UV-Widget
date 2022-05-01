package com.bunk.uvindex

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class InfoActivity : AppCompatActivity() {

	private val uvIndexSharedPreferences: UvIndexSharedPreferences by inject()
	private val getCurrentUvIndexUseCase: GetCurrentUvIndexUseCase by inject()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.info_activity)

		findViewById<TextView>(R.id.remaining_entries_in_db).apply {
			text = "Remaining Entries in DB: 27"
		}

		findViewById<TextView>(R.id.update_counter).apply {
			text = "Update Counter: ${uvIndexSharedPreferences.get()}"
		}

		val currentUvIndexTextView = findViewById<TextView>(R.id.current_uv_index)

		lifecycleScope.launch {
			val uvIndex = getCurrentUvIndexUseCase.execute()
			currentUvIndexTextView.text = "Current UvIndex: ${uvIndex.value}"
		}
	}
}