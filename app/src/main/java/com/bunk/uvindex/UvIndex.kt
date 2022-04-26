package com.bunk.uvindex

import androidx.annotation.ColorRes

sealed class UvIndex {

	object Low : UvIndex()
	object Moderate : UvIndex()
	object High : UvIndex()
	object VeryHigh : UvIndex()
	object Extreme : UvIndex()

	companion object {
		fun from(value: Int): UvIndex {
			if (value <= 2) return Low
			if (value <= 5) return Moderate
			if (value <= 7) return High
			if (value <= 10) return VeryHigh
			return Extreme
		}
	}
}

@ColorRes
fun uvColor(uvIndex: UvIndex): Int = when (uvIndex) {
	UvIndex.Extreme -> R.color.extreme
	UvIndex.High -> R.color.high
	UvIndex.Low -> R.color.low
	UvIndex.Moderate -> R.color.moderate
	UvIndex.VeryHigh -> R.color.very_high
}
