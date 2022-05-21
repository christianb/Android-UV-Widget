package com.bunk.uvindex.provider

import android.net.ConnectivityManager
import android.net.NetworkInfo

class ConnectivityProvider(
	private val connectivityManager: ConnectivityManager,
) {

	fun isNetworkAvailable(): Boolean {
		val networkInfo: NetworkInfo? = connectivityManager?.activeNetworkInfo
		return networkInfo != null && networkInfo.isConnected
	}
}