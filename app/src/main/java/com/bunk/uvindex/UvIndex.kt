package com.bunk.uvindex

import androidx.annotation.ColorRes
import kotlin.math.roundToInt

sealed class UvIndex {
	abstract val value: Double

	fun rounded(): Int = value.roundToInt()

	data class Low(override val value: Double) : UvIndex()
	data class Moderate(override val value: Double) : UvIndex()
	data class High(override val value: Double) : UvIndex()
	data class VeryHigh(override val value: Double) : UvIndex()
	data class Extreme(override val value: Double) : UvIndex()
	object Unknown : UvIndex() {
		override val value: Double = -1.0
	}

	companion object {

		fun from(preciseValue: Double?): UvIndex {
			val rounded: Int? = preciseValue?.roundToInt()

			return when {
				rounded == null -> Unknown
				rounded in 0..2 -> Low(preciseValue)
				rounded in 3..5 -> Moderate(preciseValue)
				rounded in 6..7 -> High(preciseValue)
				rounded in 8..10 -> VeryHigh(preciseValue)
				rounded >= 11 -> Extreme(preciseValue)
				else -> Unknown
			}
		}
	}

	override fun toString(): String {
		return "UvIndex(rounded=${rounded()}, precise=$value)"
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
