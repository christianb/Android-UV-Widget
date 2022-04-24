package com.bunk.uvindex.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import com.bunk.uvindex.permission.AppPermission
import com.bunk.uvindex.permission.PermissionHelper
import timber.log.Timber

class LocationRepository(
	private val locationManager: LocationManager,
	private val applicationContext: Context,
) {

	fun getLocation(): Location? {
		if (!PermissionHelper.checkPermission(applicationContext, AppPermission.ACCESS_COARSE_LOCATION, AppPermission.ACCESS_FINE_LOCATION)) {
			throw IllegalAccessException("need permission")
		}

		return getBestLocation()
	}

	@SuppressLint("MissingPermission")
	private fun getBestLocation(): Location? {
		var bestLocation: Location? = null
		for (provider in locationManager.allProviders) {
			Timber.d("try getting lastKnownLocation from provider: $provider")
			val lastKnownLocation = locationManager.getLastKnownLocation(provider)
			Timber.d("lastKnownLocation: $lastKnownLocation")

			lastKnownLocation ?: continue

			if (bestLocation == null || lastKnownLocation.accuracy < bestLocation.accuracy) {
				bestLocation = lastKnownLocation
			}
		}

		return bestLocation
	}
}