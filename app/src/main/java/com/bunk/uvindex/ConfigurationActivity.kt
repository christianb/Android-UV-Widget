package com.bunk.uvindex

import android.appwidget.AppWidgetManager
import android.content.Intent
import com.bunk.uvindex.permission.AppPermission
import com.bunk.uvindex.permission.AppPermissionResult
import com.bunk.uvindex.permission.PermissionActivity

class ConfigurationActivity : PermissionActivity() {

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
		var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
		val extras = intent.extras;
		if (extras != null) {
			appWidgetId = extras.getInt(
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