package com.bunk.uvindex

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bunk.uvindex.config.ConfigStorage
import com.bunk.uvindex.storage.UvRepository
import androidx.lifecycle.viewModelScope
import com.bunk.uvindex.config.WidgetDisplayConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant

class ConfigurationViewModel(
	private val uvRepository: UvRepository,
	private val configStorage: ConfigStorage,
	private val fetchAndUpdateWeatherDataUseCase: FetchAndUpdateWeatherDataUseCase,
) : ViewModel() {

	private val _currentUvIndex: MutableLiveData<UvIndex> = MutableLiveData()
	val currentUvIndex: LiveData<UvIndex> = _currentUvIndex

	private val _maxUvIndexNext24Hours: MutableLiveData<UvIndex> = MutableLiveData()
	val maxUvIndexNext24Hours: LiveData<UvIndex> = _maxUvIndexNext24Hours

	private val _remainingEntriesInDb: MutableLiveData<Int> = MutableLiveData()
	val remainingEntriesInDb: LiveData<Int> = _remainingEntriesInDb

	private val _widgetDisplayConfig: MutableLiveData<WidgetDisplayConfig> = MutableLiveData()
	val widgetDisplayConfig: LiveData<WidgetDisplayConfig> = _widgetDisplayConfig

	fun refresh() {
		viewModelScope.launch {
			executeRefresh()
		}
	}

	fun clearAndFetchNewData() {
		viewModelScope.launch {
			uvRepository.deleteAll()
			executeRefresh()

			delay(500L)

			fetchAndUpdateWeatherDataUseCase.execute(minRequiredEntriesInDb = Int.MAX_VALUE)
			executeRefresh()
		}
	}

	private suspend fun executeRefresh() {
		val now = Instant.now()
		_currentUvIndex.value = uvRepository.getClosestTo(now)
		_maxUvIndexNext24Hours.value = uvRepository.getMaxNext24Hours(now)
		_remainingEntriesInDb.value = uvRepository.numberOfElementsAfter(now)
		_widgetDisplayConfig.value = configStorage.read() ?: WidgetDisplayConfig.Current
	}

	fun setWidgetDisplayConfig(widgetDisplayConfig: WidgetDisplayConfig) {
		configStorage.store(widgetDisplayConfig)
		_widgetDisplayConfig.value = widgetDisplayConfig
	}
}