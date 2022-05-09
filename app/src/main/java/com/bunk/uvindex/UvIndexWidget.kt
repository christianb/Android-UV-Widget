package com.bunk.uvindex

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import com.bunk.uvindex.config.ConfigStorage
import com.bunk.uvindex.config.WidgetDisplayConfig
import com.bunk.uvindex.storage.UvRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import timber.log.Timber
import java.time.Instant

/**
 * Implementation of App Widget functionality.
 */
class UvIndexWidget : AppWidgetProvider(),
					  KoinComponent {

	private val configStorage: ConfigStorage = getKoin().get()
	private val uvRepository: UvRepository = getKoin().get()
	private val fetchAndUpdateWeatherDataUseCase: FetchAndUpdateWeatherDataUseCase = getKoin().get()

	override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
		Timber.d("onUpdate")

		val widgetDisplayConfig = configStorage.read() ?: WidgetDisplayConfig.Current

		val now = Instant.now()
		CoroutineScope(Dispatchers.Main.immediate).launch {
			val uvIndex: UvIndex = when (widgetDisplayConfig) {
				WidgetDisplayConfig.Current -> uvRepository.getClosestTo(now)
				WidgetDisplayConfig.Max24Hours -> uvRepository.getMaxNext24Hours(now)
			}
			for (appWidgetId in appWidgetIds) {
				updateAppWidget(context, appWidgetManager, appWidgetIds, uvIndex)
			}

			val minRequiredEntriesInDb = when (widgetDisplayConfig) {
				WidgetDisplayConfig.Current -> 12
				WidgetDisplayConfig.Max24Hours -> 24
			}
			fetchAndUpdateWeatherDataUseCase.execute(minRequiredEntriesInDb)
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

		if (intent.action == ACTION_CLICK) {
			ConfigurationActivity.launch(context)
		}
	}

	private fun updateAppWidget(
		context: Context,
		appWidgetManager: AppWidgetManager,
		appWidgetIds: IntArray,
		uvIndex: UvIndex,
	) {
		for (appWidgetId in appWidgetIds) {
			val remoteViews = RemoteViews(context.packageName, R.layout.uv_index_widget)

			if (uvIndex == UvIndex.Unknown) {
				remoteViews.setViewVisibility(R.id.appwidget_text, View.GONE)
				remoteViews.setViewVisibility(R.id.appwidget_wifi, View.VISIBLE)
				remoteViews.setInt(R.id.appwidget_imageview, "setColorFilter", Color.BLACK)
			} else {
				remoteViews.setViewVisibility(R.id.appwidget_text, View.VISIBLE)
				remoteViews.setViewVisibility(R.id.appwidget_wifi, View.GONE)

				remoteViews.setTextViewText(R.id.appwidget_text, uvIndex.rounded().toString())
				remoteViews.setInt(R.id.appwidget_imageview, "setColorFilter", ContextCompat.getColor(context, uvColor(uvIndex)))
			}

			val pendingIntent: PendingIntent = createOnClickPendingIntent(context, ACTION_CLICK)
			remoteViews.setOnClickPendingIntent(R.id.appwidget_imageview, pendingIntent)
			remoteViews.setOnClickPendingIntent(R.id.appwidget_wifi, pendingIntent)

			// Instruct the widget manager to update the widget
			appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
		}
	}

	private fun createOnClickPendingIntent(context: Context, action: String): PendingIntent {
		val intent = Intent(context, UvIndexWidget::class.java).apply {
			this.action = action
		}
		return PendingIntent.getBroadcast(context, 0, intent, FLAG_IMMUTABLE)
	}

	companion object {
		private const val UV_INDEX = "uv_index_extra"
		private const val ACTION_CLICK = "uvIndexWidget_onClick"

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

