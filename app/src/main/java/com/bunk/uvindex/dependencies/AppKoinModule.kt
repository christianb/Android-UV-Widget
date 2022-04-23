package com.bunk.uvindex.dependencies

import com.bunk.uvindex.api.OpenWeatherMapApi
import com.bunk.uvindex.api.RetrofitConfiguration
import org.koin.dsl.module
import retrofit2.Retrofit

val appModule = module {
	single<Retrofit> { RetrofitConfiguration.retrofit }

	single<OpenWeatherMapApi> {
		get<Retrofit>().create(OpenWeatherMapApi::class.java)
	}
}