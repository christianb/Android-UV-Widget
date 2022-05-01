package com.bunk.uvindex

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bunk.uvindex.storage.UvRepository
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.time.Instant

class InfoActivity : AppCompatActivity() {

	private val uvIndexSharedPreferences: UvIndexSharedPreferences by inject()
	private val getCurrentUvIndexUseCase: GetCurrentUvIndexUseCase by inject()
	private val uvRepository: UvRepository by inject()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.info_activity)

		val remainingEntriesTextView = findViewById<TextView>(R.id.remaining_entries_in_db)

		findViewById<TextView>(R.id.update_counter).apply {
			text = "Update Counter: ${uvIndexSharedPreferences.get()}"
		}

		val currentUvIndexTextView = findViewById<TextView>(R.id.current_uv_index)

		lifecycleScope.launch {
			currentUvIndexTextView.text = "Current UvIndex: ${getCurrentUvIndexUseCase.execute().value}"
		}

		lifecycleScope.launch {
			remainingEntriesTextView.text = "Remaining Entries in DB: ${uvRepository.countUpcoming(Instant.now())}"
		}
	}
}