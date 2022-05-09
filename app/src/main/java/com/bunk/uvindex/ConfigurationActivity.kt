package com.bunk.uvindex

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.widget.RadioGroup
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.lifecycle.lifecycleScope
import com.bunk.uvindex.config.ConfigStorage
import com.bunk.uvindex.config.WidgetDisplayConfig
import com.bunk.uvindex.permission.AppPermission
import com.bunk.uvindex.permission.AppPermissionResult
import com.bunk.uvindex.permission.PermissionActivity
import com.bunk.uvindex.storage.UvRepository
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.time.Instant

class ConfigurationActivity : PermissionActivity() {

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
		@IdRes
		val radioButtonId: Int = when (configStorage.read() ?: WidgetDisplayConfig.Current) {
			WidgetDisplayConfig.Current -> R.id.current_radio_button
			WidgetDisplayConfig.Max24Hours -> R.id.max24Hours_radio_button
		}

		findViewById<RadioGroup>(R.id.radio_group).apply {
			clearCheck()
			check(radioButtonId)
			setOnCheckedChangeListener { _, resId ->
				val widgetDisplayConfig = if (resId == R.id.current_radio_button) WidgetDisplayConfig.Current
				else WidgetDisplayConfig.Max24Hours
				configStorage.store(widgetDisplayConfig)
				UvIndexWidget.update(context = this@ConfigurationActivity)
			}
		}
	}

	override fun onStart() {
		super.onStart()

		requestPermission(
			AppPermission.ACCESS_COARSE_LOCATION,
			AppPermission.ACCESS_BACKGROUND_LOCATION
		)
	}

	override fun onPermissionResult(appPermissionResults: List<AppPermissionResult>) {
		super.onPermissionResult(appPermissionResults)

		if (allGranted(appPermissionResults)) {
			UvIndexWidget.update(context = this)
			configurationResult()
		}
	}

	private fun configurationResult() {
		val extras = intent.extras;
		if (extras != null) {
			val appWidgetId = extras.getInt(
				AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID);

			if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
				finish()
			}

			val resultValue = Intent().apply {
				putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
			}

			setResult(RESULT_OK, resultValue);

			finish()
		}
	}
}