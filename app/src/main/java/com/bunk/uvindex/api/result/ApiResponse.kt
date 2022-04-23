package com.bunk.uvindex.api.result

sealed class ApiResponse<out T> {

	data class Success<T>(val data: T) : ApiResponse<T>()

	data class Failure(
		val throwable: Throwable,
	) : ApiResponse<Nothing>() {
		constructor(message: String) : this(Throwable(message))
	}

	fun <S> mapData(dataMapper: (T) -> S): ApiResponse<S> {
		return when (this) {
			is Success -> Success(dataMapper.invoke(data))
			is Failure -> this
		}
	}

	fun mapToUnit(): ApiResponse<Unit> = mapData {}

	val dataOrNull: T?
		get() = (this as? Success)?.data

	val throwableOrNull: Throwable?
		get() = (this as? Failure)?.throwable

	fun onSuccess(callback: (T) -> Unit): ApiResponse<T> {
		(this as? Success)?.dataOrNull?.let { data ->
			callback.invoke(data)
		}
		return this
	}

	fun onFailure(callback: (Throwable) -> Unit): ApiResponse<T> {
		(this as? Failure)?.throwable?.let { throwable ->
			callback.invoke(throwable)
		}
		return this
	}
}