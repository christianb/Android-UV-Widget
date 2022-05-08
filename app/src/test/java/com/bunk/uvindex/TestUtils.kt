package com.bunk.uvindex

import android.location.Location
import com.bunk.uvindex.storage.database.UvEntity
import io.mockk.every
import io.mockk.mockk

fun createUvEntity(
	dt: Long = 0,
	uvIndex: Double = 0.0,
	longitude: Double = 0.0,
	latitude: Double = 0.0,
): UvEntity {
	return UvEntity(dt = dt, uvIndex = uvIndex, longitude = longitude, latitude = latitude)
}

fun mockkLocation(latitude: Double = 0.0, longitude: Double = 0.0): Location {
	val location: Location = mockk()
	every { location.latitude } returns latitude
	every { location.longitude } returns longitude
	return location
}