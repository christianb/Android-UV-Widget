package com.bunk.uvindex.dependencies

import com.bunk.uvindex.api.OpenWeatherMapApi
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
	single<Retrofit> {
		Retrofit.Builder()
			.baseUrl("https://api.openweathermap.org")
			.addConverterFactory(GsonConverterFactory.create())
			.build()
	}

	single<OpenWeatherMapApi> {
		get<Retrofit>().create(OpenWeatherMapApi::class.java)
	}
}