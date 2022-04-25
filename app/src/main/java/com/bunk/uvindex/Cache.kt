package com.bunk.uvindex

class Cache<T> {

	var updatedAt: Long? = null
		private set(value) {
			field = value
		}

	var value: T? = null
		set(value) {
			field = value
			updatedAt = System.currentTimeMillis()
		}

	fun isStale(): Boolean {
		val localUpdatedAt = updatedAt ?: return true
		return localUpdatedAt + STALE_THRESHOLD < System.currentTimeMillis()
	}

	companion object {
		private const val HOURS: Long = 1000L * 60 * 60
		private const val STALE_THRESHOLD: Long = HOURS * 2
	}
}