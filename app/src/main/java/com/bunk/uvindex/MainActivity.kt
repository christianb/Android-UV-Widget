package com.bunk.uvindex

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.bunk.uvindex.api.OpenWeatherMapApi
import com.bunk.uvindex.api.WeatherData
import com.bunk.uvindex.ui.theme.UvIndexTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {

	private val openWeatherMapApi: OpenWeatherMapApi by inject()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			UvIndexTheme {
				// A surface container using the 'background' color from the theme
				Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
					Greeting("Android")
				}
			}
		}
	}

	override fun onStart() {
		super.onStart()
		Timber.d("onStart")

		// Miami
		// latitude = "25.761681"
		// longitude = "-80.191788"

		// Berlin
		// latitude = "52.520008",
		// longitude = "13.404954",

		lifecycleScope.launch {
			val response: WeatherData? = openWeatherMapApi.getData(
				latitude = "25.761681",
				longitude = "-80.191788",
				apiKey = BuildConfig.API_KEY
			).dataOrNull

			val uvIndex: Int? = response?.current?.uvi?.roundToInt()

			UvIndexWidget.send(this@MainActivity, uvIndex ?: -1)
		}
	}
}

@Composable
fun Greeting(name: String) {
	Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
	UvIndexTheme {
		Greeting("Android")
	}
}