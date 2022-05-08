package com.bunk.uvindex

import android.os.Bundle
import android.widget.RadioGroup
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bunk.uvindex.config.ConfigStorage
import com.bunk.uvindex.config.WidgetDisplayConfig
import com.bunk.uvindex.storage.UvRepository
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.time.Instant

class InfoActivity : AppCompatActivity() {

	private val uvIndexSharedPreferences: UvIndexSharedPreferences by inject()
	private val getCurrentUvIndexUseCase: GetCurrentUvIndexUseCase by inject()
	private val uvRepository: UvRepository by inject()
	private val configStorage: ConfigStorage by inject()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.info_activity)

		initRadioGroup()

		val remainingEntriesTextView = findViewById<TextView>(R.id.remaining_entries_in_db)

		findViewById<TextView>(R.id.update_counter).apply {
			text = "Update Counter: ${uvIndexSharedPreferences.get()}"
		}

		val currentUvIndexTextView = findViewById<TextView>(R.id.current_uv_index)

		lifecycleScope.launch {
			currentUvIndexTextView.text = "Current UvIndex: ${getCurrentUvIndexUseCase.execute().value}"
		}

		lifecycleScope.launch {
			remainingEntriesTextView.text = "Remaining Entries in DB: ${uvRepository.numberOfElementsAfter(Instant.now())}"
		}
	}

	private fun initRadioGroup() {
		val radioGroup = findViewById<RadioGroup>(R.id.radio_group)
		radioGroup.clearCheck()
		@IdRes
		val radioButtonId: Int = when (configStorage.read() ?: WidgetDisplayConfig.Current) {
			WidgetDisplayConfig.Current -> R.id.current_radio_button
			WidgetDisplayConfig.Max24Hours -> R.id.max24Hours_radio_button
		}
		radioGroup.check(radioButtonId)

		radioGroup.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
			override fun onCheckedChanged(radioGroup: RadioGroup, p1: Int) {
				val widgetDisplayConfig = if (p1 == R.id.current_radio_button) WidgetDisplayConfig.Current
				else WidgetDisplayConfig.Max24Hours

				configStorage.store(widgetDisplayConfig)
			}
		})
	}
}