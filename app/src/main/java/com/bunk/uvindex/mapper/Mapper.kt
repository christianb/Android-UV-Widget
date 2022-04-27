package com.bunk.uvindex.mapper

import android.location.Location
import com.bunk.uvindex.api.WeatherData
import com.bunk.uvindex.storage.database.UvEntity

fun WeatherData.Hourly.toUvEntity(longitude: Double, latitude: Double): UvEntity = UvEntity(
	dt = dt, uvIndex = uvi, longitude = longitude, latitude = latitude
)

fun UvEntity.getLocation(): Location {
	return Location("").also {
		it.longitude = longitude
		it.latitude = latitude
	}
}