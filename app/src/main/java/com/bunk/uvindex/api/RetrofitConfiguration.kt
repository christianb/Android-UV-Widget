package com.bunk.uvindex.api

import com.bunk.uvindex.api.result.adapter.ApiResponseCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitConfiguration {
	val retrofit: Retrofit = Retrofit.Builder()
		.baseUrl("https://api.openweathermap.org")
		.addConverterFactory(GsonConverterFactory.create())
		.addCallAdapterFactory(ApiResponseCallAdapterFactory())
		.build()
}