package com.bunk.uvindex.permission

import android.Manifest

enum class AppPermission(
	val androidPermission: String,
) {
//	ACCESS_FINE_LOCATION(Manifest.permission.ACCESS_FINE_LOCATION),
	ACCESS_COARSE_LOCATION(Manifest.permission.ACCESS_COARSE_LOCATION),
	ACCESS_BACKGROUND_LOCATION(Manifest.permission.ACCESS_BACKGROUND_LOCATION),

	;

	companion object {
		fun from(androidPermission: String): AppPermission = values().first { it.androidPermission == androidPermission }
	}
}