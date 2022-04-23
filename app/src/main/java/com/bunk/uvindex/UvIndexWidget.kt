package com.bunk.uvindex

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import timber.log.Timber

/**
 * Implementation of App Widget functionality.
 */
class UvIndexWidget : AppWidgetProvider() {

	override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
		for (appWidgetId in appWidgetIds) {
			updateAppWidget(context, appWidgetManager, appWidgetIds, null)
		}
	}

	override fun onEnabled(context: Context) {
		// Enter relevant functionality for when the first widget is created
	}

	override fun onDisabled(context: Context) {
		// Enter relevant functionality for when the last widget is disabled
	}

	override fun onReceive(context: Context, intent: Intent) {
		super.onReceive(context, intent)

		val appWidgetManager = AppWidgetManager.getInstance(context)
		val componentName = ComponentName(context, UvIndexWidget::class.java)
		val appWidgetIds: IntArray = AppWidgetManager.getInstance(context).getAppWidgetIds(componentName)

		val uvIndex = read(intent)
		updateAppWidget(context, appWidgetManager, appWidgetIds, uvIndex)
	}

	private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray, uvIndex: Int?) {
		uvIndex ?: return

		for (appWidgetId in appWidgetIds) {
			val views = RemoteViews(context.packageName, R.layout.uv_index_widget)
			views.setTextViewText(R.id.appwidget_text, "UV-Index: $uvIndex")
			Timber.d("updateWidget: $uvIndex")

			// Instruct the widget manager to update the widget
			appWidgetManager.updateAppWidget(appWidgetId, views)
		}
	}

	companion object {
		const val UV_INDEX = "uv_index_extra"

		private fun createIntent(context: Context): Intent = Intent(context, UvIndexWidget::class.java).apply {
			action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
			val ids = AppWidgetManager.getInstance(context).getAppWidgetIds(ComponentName(context.applicationContext, UvIndexWidget::class.java))
			putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
		}

		fun send(context: Context, value: Int) {
			val intent = createIntent(context).apply {
				putExtra(UV_INDEX, value)
			}
			context.sendBroadcast(intent)
		}

		fun read(intent: Intent): Int = intent.getIntExtra(UV_INDEX, -1)
	}
}

