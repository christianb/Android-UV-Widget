package com.bunk.uvindex

import androidx.annotation.ColorRes

sealed class UvIndex {
	abstract val value: Int

	data class Low(override val value: Int) : UvIndex()
	data class Moderate(override val value: Int) : UvIndex()
	data class High(override val value: Int) : UvIndex()
	data class VeryHigh(override val value: Int) : UvIndex()
	data class Extreme(override val value: Int) : UvIndex()
	object Unknown : UvIndex() {
		override val value: Int = -1
	}

	companion object {
		fun from(value: Int?): UvIndex = when {
			value == null -> Unknown
			value in 0..2 -> Low(value)
			value in 3..5 -> Moderate(value)
			value in 6..7 -> High(value)
			value in 8..10 -> VeryHigh(value)
			value >= 11 -> Extreme(value)
			else -> Unknown
		}
	}

	override fun toString(): String {
		return "UvIndex($value)"
	}
}

@ColorRes
fun uvColor(uvIndex: UvIndex): Int = when (uvIndex) {
	is UvIndex.Extreme -> R.color.extreme
	is UvIndex.High -> R.color.high
	is UvIndex.Low -> R.color.low
	is UvIndex.Moderate -> R.color.moderate
	is UvIndex.VeryHigh -> R.color.very_high
	UvIndex.Unknown -> R.color.white
}
