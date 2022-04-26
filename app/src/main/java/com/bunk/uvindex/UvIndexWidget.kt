package com.bunk.uvindex

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import com.bunk.uvindex.storage.database.UvDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import timber.log.Timber

/**
 * Implementation of App Widget functionality.
 */
class UvIndexWidget : AppWidgetProvider(), KoinComponent {

	private val getCurrentUvIndexUseCase: GetCurrentUvIndexUseCase = getKoin().get()
	private val uvDao: UvDao = getKoin().get()

	override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
		Timber.d("onUpdate")
		CoroutineScope(Dispatchers.Main.immediate).launch {
			val uvIndex: UvIndex = getCurrentUvIndexUseCase.execute()
			for (appWidgetId in appWidgetIds) {
				updateAppWidget(context, appWidgetManager, appWidgetIds, uvIndex)
			}
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
	}

	private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray, uvIndex: UvIndex) {
		for (appWidgetId in appWidgetIds) {
			val remoteViews = RemoteViews(context.packageName, R.layout.uv_index_widget)
			remoteViews.setTextViewText(R.id.appwidget_text, uvIndex.value.toString())
			Timber.d("updateWidget: $uvIndex")

			remoteViews.setImageViewResource(R.id.appwidget_imageview, R.drawable.uv_index_circular_background)
			remoteViews.setInt(R.id.appwidget_imageview, "setColorFilter", ContextCompat.getColor(context, uvColor(uvIndex)))

			// Instruct the widget manager to update the widget
			appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
		}
	}

	companion object {
		const val UV_INDEX = "uv_index_extra"

		private fun createIntent(context: Context, action: String): Intent = Intent(context, UvIndexWidget::class.java).apply {
			this.action = action
			val ids = AppWidgetManager.getInstance(context).getAppWidgetIds(ComponentName(context.applicationContext, UvIndexWidget::class.java))
			putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
		}

		fun send(context: Context, value: Int) {
			val intent = createIntent(context, action = AppWidgetManager.ACTION_APPWIDGET_UPDATE).apply {
				putExtra(UV_INDEX, value)
			}
			context.sendBroadcast(intent)
		}

		fun update(context: Context) {
			context.sendBroadcast(createIntent(context, action = AppWidgetManager.ACTION_APPWIDGET_UPDATE))
		}

		fun read(intent: Intent): Int = intent.getIntExtra(UV_INDEX, -1)
	}
}

