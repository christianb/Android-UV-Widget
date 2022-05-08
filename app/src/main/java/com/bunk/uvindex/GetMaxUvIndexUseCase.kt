package com.bunk.uvindex

import com.bunk.uvindex.storage.UvRepository
import java.time.Instant

class GetMaxUvIndexUseCase(
	private val fetchAndUpdateWeatherDataUseCase: FetchAndUpdateWeatherDataUseCase,
	private val uvRepository: UvRepository
) {

	suspend fun execute(): UvIndex {
		fetchAndUpdateWeatherDataUseCase.execute(minRequiredEntriesInDb = 24)

		val uvIndex: Double? = uvRepository.getMaxWithin24Hours(now = Instant.now())?.uvIndex
		return UvIndex.from(uvIndex)
	}
}