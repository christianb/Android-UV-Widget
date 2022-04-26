package com.bunk.uvindex.mapper

import com.bunk.uvindex.api.WeatherData
import com.bunk.uvindex.storage.UvEntity

fun WeatherData.Hourly.toUvEntity(): UvEntity = UvEntity(dt = dt, uvIndex = uvi)