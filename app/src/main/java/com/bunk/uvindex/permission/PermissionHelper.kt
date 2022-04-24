package com.bunk.uvindex.permission

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import timber.log.Timber

class PermissionHelper(
//	private val activity: Activity,
) {
	private val listeners: MutableSet<Listener> = mutableSetOf()

	fun requestPermissions(activity: Activity, vararg appPermissions: AppPermission) {
		if (checkPermission(activity, *appPermissions)) {
			val appPermissionResults: List<AppPermissionResult> = appPermissions.map {
				AppPermissionResult(it, PermissionResult.Granted)
			}

			notifyPermissionResults(appPermissionResults)
			return
		}

		val permissionStrings = appPermissions.map { it.androidPermission }.toTypedArray()
		ActivityCompat.requestPermissions(activity, permissionStrings, 0)
	}

	fun onRequestPermissionsResult(activity: Activity, androidPermissions: Array<out String>, grantResults: IntArray) {
		val appPermissionResult: List<AppPermissionResult> = androidPermissions.mapIndexed { index, androidPermission ->
			val appPermission = AppPermission.from(androidPermission)
			Timber.d("appPermission = ${appPermission.androidPermission}")
			val permissionResult = when {
				grantResults[index] == PackageManager.PERMISSION_GRANTED -> PermissionResult.Granted
				ActivityCompat.shouldShowRequestPermissionRationale(activity, androidPermission) -> PermissionResult.Denied.ShowRationale
				else -> PermissionResult.Denied.DoNotAskAgain
			}
			Timber.d("permissionResult = $permissionResult")
			Timber.d("grantResult = ${grantResults[index]}")

			AppPermissionResult(appPermission, permissionResult)
		}

		notifyPermissionResults(appPermissionResult)
	}

	fun registerListener(listener: Listener) {
		listeners.add(listener)
	}

	fun unregisterListener(listener: Listener) {
		listeners.remove(listener)
	}

	private fun notifyPermissionResults(appPermissionResults: List<AppPermissionResult>) {
		for (listener in listeners) {
			listener.onPermissionResult(appPermissionResults)
		}
	}

	interface Listener {
		fun onPermissionResult(appPermissionResults: List<AppPermissionResult>)
	}

	companion object {
		fun checkPermission(context: Context, vararg appPermissions: AppPermission): Boolean {
			for (appPermission in appPermissions) {
				if (!hasPermission(context, appPermission)) return false
			}
			return true
		}

		private fun hasPermission(context: Context, appPermission: AppPermission): Boolean =
			ContextCompat.checkSelfPermission(context, appPermission.androidPermission) == PackageManager.PERMISSION_GRANTED
	}
}