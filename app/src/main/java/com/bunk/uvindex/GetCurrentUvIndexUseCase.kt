package com.bunk.uvindex

import com.bunk.uvindex.storage.UvRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant

class GetCurrentUvIndexUseCase(
	private val fetchAndUpdateWeatherDataUseCase: FetchAndUpdateWeatherDataUseCase,
	private val uvRepository: UvRepository
) {

	suspend fun execute(): UvIndex = withContext(Dispatchers.IO) { // TODO does room require going of the main thread?
		fetchAndUpdateWeatherDataUseCase.execute(minRequiredEntriesInDb = 12)

		val uvIndex: Double? = uvRepository.getClosestTo(Instant.now())?.uvIndex
		return@withContext UvIndex.from(uvIndex)
	}
}