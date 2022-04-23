package com.bunk.uvindex.api

import com.bunk.uvindex.api.result.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherMapApi {

	@GET("data/2.5/onecall")
	suspend fun getWeather(
		@Query("lat") latitude: String,
		@Query("lon") longitude: String,
		@Query("appid") apiKey: String,
	): ApiResponse<WeatherData>
}