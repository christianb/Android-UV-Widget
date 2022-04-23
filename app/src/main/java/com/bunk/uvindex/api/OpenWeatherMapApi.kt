package com.bunk.uvindex.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherMapApi {

	@GET("data/2.5/onecall")
	fun getData(
		@Query("lat") latitude: String,
		@Query("lon") longitude: String,
		@Query("appid") apiKey: String,
	): Call<WeatherData>
}