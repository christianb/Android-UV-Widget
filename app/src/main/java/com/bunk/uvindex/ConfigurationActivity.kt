package com.bunk.uvindex

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import androidx.annotation.IdRes
import com.bunk.uvindex.config.WidgetDisplayConfig
import com.bunk.uvindex.permission.AppPermission
import com.bunk.uvindex.permission.AppPermissionResult
import com.bunk.uvindex.permission.PermissionActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class ConfigurationActivity : PermissionActivity() {

	private val viewModel: ConfigurationViewModel by viewModel()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.info_activity)

		val currentUvIndexTextView = findViewById<TextView>(R.id.current_uv_index)

		viewModel.currentUvIndex.observe(this) { uvIndex ->
			currentUvIndexTextView.text = "Current UvIndex: $uvIndex"
		}

		val maxUvIndexTextView = findViewById<TextView>(R.id.max_uv_index)
		viewModel.maxUvIndexNext24Hours.observe(this) { uvIndex ->
			maxUvIndexTextView.text = "Max UvIndex: $uvIndex"
		}

		val remainingEntriesTextView = findViewById<TextView>(R.id.remaining_entries_in_db)
		viewModel.remainingEntriesInDb.observe(this) { uvIndex ->
			remainingEntriesTextView.text = "Remaining Entries in DB: $uvIndex"
		}

		initRadioGroup()

		findViewById<Button>(R.id.clear_and_refresh_button).setOnClickListener {
			viewModel.clearAndFetchNewData()
		}
	}

	private fun initRadioGroup() {
		val radioGroup: RadioGroup = findViewById<RadioGroup>(R.id.radio_group).apply {
			clearCheck()

			setOnCheckedChangeListener { _, resId ->
				val widgetDisplayConfig = if (resId == R.id.current_radio_button) WidgetDisplayConfig.Current
				else WidgetDisplayConfig.Max24Hours
				viewModel.setWidgetDisplayConfig(widgetDisplayConfig)
				UvIndexWidget.update(context = this@ConfigurationActivity)
			}
		}

		viewModel.widgetDisplayConfig.observe(this) {
			@IdRes
			val radioButtonId: Int = when (it) {
				WidgetDisplayConfig.Current -> R.id.current_radio_button
				WidgetDisplayConfig.Max24Hours -> R.id.max24Hours_radio_button
			}

			radioGroup.check(radioButtonId)
		}
	}

	override fun onStart() {
		super.onStart()
		viewModel.refresh()

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

	companion object {
		fun launch(context: Context) {
			val intent = Intent(context, ConfigurationActivity::class.java)
			intent.addFlags(FLAG_ACTIVITY_CLEAR_TASK)
			intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
			context.startActivity(intent)
		}
	}
}