package com.bunk.uvindex.permission

sealed class PermissionResult {

	object Granted : PermissionResult()

	sealed class Denied : PermissionResult() {
		object DoNotAskAgain : Denied()
		object ShowRationale : Denied()
	}
}