package com.bunk.uvindex.dependencies

import android.content.Context
import android.location.LocationManager
import com.bunk.uvindex.GetCurrentUvIndexUseCase
import com.bunk.uvindex.api.OpenWeatherMapApi
import com.bunk.uvindex.api.RetrofitConfiguration
import com.bunk.uvindex.api.WeatherRepository
import com.bunk.uvindex.location.LocationRepository
import com.bunk.uvindex.permission.PermissionHelper
import com.bunk.uvindex.storage.database.AppDatabase
import com.bunk.uvindex.storage.database.UvDao
import com.bunk.uvindex.storage.UvRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import retrofit2.Retrofit

val appModule = module {
	// Android Services
	single<LocationManager> { androidApplication().getSystemService(Context.LOCATION_SERVICE) as LocationManager }

	// Retrofit
	single<Retrofit> { RetrofitConfiguration.retrofit }
	single<OpenWeatherMapApi> { get<Retrofit>().create(OpenWeatherMapApi::class.java) }

	// Repositories
	single<WeatherRepository> { WeatherRepository(openWeatherMapApi = get()) }
	factory<LocationRepository> { LocationRepository(locationManager = get(), applicationContext = androidApplication()) }
	factory<UvRepository> { UvRepository(dao = get()) }

	// UseCases
	factory<GetCurrentUvIndexUseCase> {
		GetCurrentUvIndexUseCase(
			weatherRepository = get(),
			locationRepository = get(),
			uvRepository = get()
		)
	}

	// Permissions
	factory<PermissionHelper> { PermissionHelper() }

	// Room
	single<AppDatabase> { AppDatabase.build(applicationContext = androidApplication()) }
	single<UvDao> { get<AppDatabase>().uvDao() }
}