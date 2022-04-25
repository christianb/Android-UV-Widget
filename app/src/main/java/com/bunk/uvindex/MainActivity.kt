package com.bunk.uvindex

import android.os.Bundle
import android.text.format.DateFormat
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.bunk.uvindex.api.WeatherData
import com.bunk.uvindex.api.WeatherRepository
import com.bunk.uvindex.location.LocationRepository
import com.bunk.uvindex.permission.AppPermission
import com.bunk.uvindex.permission.PermissionActivity
import com.bunk.uvindex.ui.theme.UvIndexTheme
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import timber.log.Timber
import kotlin.math.roundToInt

class MainActivity : PermissionActivity() {

	private val locationRepository: LocationRepository by inject()
	private val weatherRepository: WeatherRepository by inject()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			UvIndexTheme {
				// A surface container using the 'background' color from the theme
				Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
					Column {
						val last: Cache<WeatherData> = weatherRepository.last()
						Text(text = "UvIndex: ${last.value?.current?.uvi?.roundToInt() ?: -1}")
						Text(text = "last synced at: ${convertDate(last.updatedAt ?: 0, "dd/MM/yyyy hh:mm:ss")}")
					}
				}
			}
		}

		requestPermission(
			AppPermission.ACCESS_COARSE_LOCATION,
			AppPermission.ACCESS_BACKGROUND_LOCATION
		)
	}

	fun convertDate(dateInMilliseconds: Long, dateFormat: String?): String? {
		return DateFormat.format(dateFormat, dateInMilliseconds).toString()
	}

	override fun onStart() {
		super.onStart()

		Timber.d("onStart")

		Timber.d("location: ${locationRepository.getLocation()}")

		// Miami
		// latitude = "25.761681"
		// longitude = "-80.191788"

		// Berlin
		// latitude = "52.520008",
		// longitude = "13.404954",

		lifecycleScope.launch {
//			val response: WeatherData? = openWeatherMapApi.getData(
//				latitude = "52.520008",
//				longitude = "13.404954",
//				apiKey = BuildConfig.API_KEY
//			).dataOrNull
//
//			val uvIndex: Int? = response?.current?.uvi?.roundToInt()

//			UvIndexWidget.send(this@MainActivity, uvIndex ?: -1)
		}
	}
}

@Composable
fun UvIndex(value: Int?) {

}

//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//	UvIndexTheme {
//		Greeting("Android")
//	}
//}

