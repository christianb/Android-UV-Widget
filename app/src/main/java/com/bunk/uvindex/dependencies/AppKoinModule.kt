package com.bunk.uvindex.dependencies

import android.content.Context
import android.location.LocationManager
import android.net.ConnectivityManager
import androidx.core.content.ContextCompat
import com.bunk.uvindex.ConfigurationViewModel
import com.bunk.uvindex.FetchAndUpdateWeatherDataUseCase
import com.bunk.uvindex.UvIndexSharedPreferences
import com.bunk.uvindex.api.OpenWeatherMapApi
import com.bunk.uvindex.api.RetrofitConfiguration
import com.bunk.uvindex.api.WeatherRepository
import com.bunk.uvindex.config.ConfigStorage
import com.bunk.uvindex.location.LocationRepository
import com.bunk.uvindex.permission.PermissionHelper
import com.bunk.uvindex.provider.ConnectivityProvider
import com.bunk.uvindex.storage.database.AppDatabase
import com.bunk.uvindex.storage.database.UvDao
import com.bunk.uvindex.storage.UvRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import retrofit2.Retrofit

val appModule = module {

	// System Service
	single<LocationManager> { androidApplication().getSystemService(Context.LOCATION_SERVICE) as LocationManager }
	single<ConnectivityManager>() { ContextCompat.getSystemService(androidApplication(), ConnectivityManager::class.java) as ConnectivityManager }

	// Retrofit
	single<Retrofit> { RetrofitConfiguration.retrofit }
	single<OpenWeatherMapApi> { get<Retrofit>().create(OpenWeatherMapApi::class.java) }

	// Repositories
	single<WeatherRepository> {
		WeatherRepository(
			openWeatherMapApi = get(),
			connectivityProvider = get()
		)
	}

	factory<LocationRepository> {
		LocationRepository(
			applicationContext = androidApplication(),
			locationManager = get(),
		)
	}
	factory<UvRepository> { UvRepository(dao = get()) }

	// UseCases
	factory<FetchAndUpdateWeatherDataUseCase> {
		FetchAndUpdateWeatherDataUseCase(
			uvRepository = get(),
			locationRepository = get(),
			weatherRepository = get(),
		)
	}

	// Permissions
	factory<PermissionHelper> { PermissionHelper() }

	// Room
	single<AppDatabase> { AppDatabase.build(applicationContext = androidApplication()) }
	single<UvDao> { get<AppDatabase>().uvDao() }

	single { UvIndexSharedPreferences(context = androidApplication()) }

	factory<ConfigStorage> {
		ConfigStorage(applicationContext = androidApplication())
	}

	factory<ConfigurationViewModel> {
		ConfigurationViewModel(
			uvRepository = get(),
			configStorage = get(),
			fetchAndUpdateWeatherDataUseCase = get()
		)
	}

	single<ConnectivityProvider> {
		// must be single!
		ConnectivityProvider(connectivityManager = get())
	}
}