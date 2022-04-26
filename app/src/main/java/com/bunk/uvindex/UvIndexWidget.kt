package com.bunk.uvindex

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.bunk.uvindex.mapper.toUvEntity
import com.bunk.uvindex.storage.UvDao
import com.bunk.uvindex.storage.UvEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import timber.log.Timber
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt

/**
 * Implementation of App Widget functionality.
 */
class UvIndexWidget : AppWidgetProvider(), KoinComponent {

	private val getWeatherUseCase: GetWeatherUseCase = getKoin().get()
	private val uvDao: UvDao = getKoin().get()

	override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
		Timber.d("onUpdate")

		CoroutineScope(Dispatchers.Main.immediate).launch {

			val closest: UvEntity? =  withContext(Dispatchers.IO) {
				// clean up DB
				val nowInSeconds: Long = System.currentTimeMillis() / 1000
				uvDao.delete(nowInSeconds)

				// count elements in DB
				val count: Int = uvDao.count(nowInSeconds)
				Timber.d("$count entries in DB")

				if (count <= THRESHOLD_MAKING_NEW_REQUEST) {
					Timber.i("too few elements in DB, make request")

					val weatherData = getWeatherUseCase.execute()
					Timber.d("weatherData: $weatherData")

					// insert response in DB
					val uvEntities: List<UvEntity> = weatherData?.hourly?.map { it.toUvEntity() } ?: emptyList()
					uvDao.insertAll(uvEntities)
				}

				// get all entries from DB
				val all: List<UvEntity> = uvDao.getAll(nowInSeconds)
				Timber.d("all = $all")

				getClosest(all, nowInSeconds)
			}

			Timber.d("closestToNow = $closest (date: ${toDate(closest?.dt)})")

			val uvIndex: Int = closest?.uvIndex?.roundToInt() ?: return@launch

			for (appWidgetId in appWidgetIds) {
				updateAppWidget(context, appWidgetManager, appWidgetIds, uvIndex)
			}
		}
	}

	private fun getClosest(all: List<UvEntity>, nowInSeconds: Long): UvEntity? {
		var closest: UvEntity? = null
		for (element in all) {
			if (closest == null || element.dt - nowInSeconds < closest.dt - nowInSeconds) {
				closest = element
			}
		}

		return closest
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

	private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray, uvIndex: Int) {
		for (appWidgetId in appWidgetIds) {
			val remoteViews = RemoteViews(context.packageName, R.layout.uv_index_widget)
			remoteViews.setTextViewText(R.id.appwidget_text, uvIndex.toString())
			Timber.d("updateWidget: $uvIndex")

			remoteViews.setImageViewResource(R.id.appwidget_imageview, R.drawable.uv_index_circular_background);
			remoteViews.setInt(R.id.appwidget_imageview, "setColorFilter", mapUvIndexToColor(uvIndex, context))

			// Instruct the widget manager to update the widget
			appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
		}
	}

	/**
	 * from https://en.wikipedia.org/wiki/Ultraviolet_index
	 */
	@ColorInt
	private fun mapUvIndexToColor(uvIndex: Int, context: Context): Int {
		val uvColor = uvColor(UvIndex.from(uvIndex))
		return ContextCompat.getColor(context, uvColor)
	}

	@SuppressLint("NewApi")
	private fun toDate(unixTime: Long?): String { // TODO set minSDK to 26
		unixTime ?: return "null"
		val instant: Instant = Instant.ofEpochSecond(unixTime)
		val zoneId: ZoneId = TimeZone.getDefault().toZoneId()
		val zonedDateTime: ZonedDateTime = instant.atZone(zoneId)
		val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-uuuu hh:mm a")
		return zonedDateTime.format(formatter)
	}

	companion object {
		const val UV_INDEX = "uv_index_extra"
		const val THRESHOLD_MAKING_NEW_REQUEST = 12

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

