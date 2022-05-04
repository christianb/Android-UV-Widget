package com.bunk.uvindex.api

import com.bunk.uvindex.BuildConfig
import com.bunk.uvindex.api.result.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherMapApi {

	@GET("data/2.5/onecall")
	suspend fun getWeather(
		@Query("lat") latitude: Double,
		@Query("lon") longitude: Double,
		@Query("appid") apiKey: String = BuildConfig.API_KEY,
		@Query("exclude") exclude: String = "current,minutely,daily,alerts"
	): ApiResponse<WeatherData>
}