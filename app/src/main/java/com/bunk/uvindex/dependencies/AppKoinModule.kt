package com.bunk.uvindex.dependencies

import android.app.Activity
import android.content.Context
import android.location.LocationManager
import com.bunk.uvindex.GetWeatherUseCase
import com.bunk.uvindex.api.OpenWeatherMapApi
import com.bunk.uvindex.api.RetrofitConfiguration
import com.bunk.uvindex.api.WeatherRepository
import com.bunk.uvindex.location.LocationRepository
import com.bunk.uvindex.permission.PermissionHelper
import com.bunk.uvindex.storage.AppDatabase
import com.bunk.uvindex.storage.UvDao
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

	// UseCases
	factory<GetWeatherUseCase> {
		GetWeatherUseCase(
			weatherRepository = get(),
			locationRepository = get()
		)
	}

	// Permissions
	factory<PermissionHelper> { PermissionHelper() }

	// Room
	single<AppDatabase> { AppDatabase.build(applicationContext = androidApplication()) }
	single<UvDao> { get<AppDatabase>().uvDao() }
}