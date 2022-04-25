package com.bunk.uvindex.permission

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

abstract class PermissionActivity : ComponentActivity(),
									PermissionHelper.Listener {

	private val permissionHelper: PermissionHelper by inject { parametersOf(this) }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		permissionHelper.registerListener(this)
	}

	override fun onDestroy() {
		super.onDestroy()
		permissionHelper.unregisterListener(this)
	}

	override fun onPermissionResult(appPermissionResults: List<AppPermissionResult>) {
		val doNotAskAgain = appPermissionResults.filter { it.permissionResult == PermissionResult.Denied.DoNotAskAgain }
		val showRationale = appPermissionResults.filter { it.permissionResult == PermissionResult.Denied.ShowRationale }

		when {
			showRationale.isNotEmpty() -> showAskPermissionsDialog(*showRationale.map { it.appPermission }.toTypedArray())
			doNotAskAgain.isNotEmpty() -> showGoToSettingsDialog()
			else -> {
				// all permissions granted
			}
		}
	}

	fun allGranted(appPermissionResults: List<AppPermissionResult>): Boolean =
		appPermissionResults.all { it.permissionResult == PermissionResult.Granted }

	/**
	 * Just a helper method in case the user blocks permission. It goes to your application settings page for the user to enable permission again.
	 */
	private fun goToSettings() {
		startActivity(Intent(
			Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
			Uri.fromParts("package", packageName, null))
		)
	}

	private fun showAskPermissionsDialog(vararg appPermissions: AppPermission) {
		AlertDialog.Builder(this)
			.setTitle("Background Location")
			.setMessage("Please select 'allow always' for location permission in settings.")
			.setPositiveButton("Settings") { _, _ ->
//				permissionHelper.requestPermissions(activity = this, *appPermissions)
				goToSettings()
			}
			.setNegativeButton("No thanks") { _, _ ->
				finish()
			}
			.setCancelable(false)
			.create()
			.show()
	}

	private fun showGoToSettingsDialog() {
		AlertDialog.Builder(this)
			.setTitle("Go to Settings")
			.setMessage("You must grant the permissions or the widget will not work. Please go to settings to change them.")
			.setPositiveButton("Open Settings") { _, _ ->
				goToSettings()
				finish()
			}
			.setNegativeButton("No thanks") { _, _ ->
				finish()
			}
			.setCancelable(false)
			.create()
			.show()
	}

	fun requestPermission(vararg requiredPermissions: AppPermission) {
		permissionHelper.requestPermissions(activity = this, *requiredPermissions)
	}

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		permissionHelper.onRequestPermissionsResult(activity = this, permissions, grantResults)
	}
}