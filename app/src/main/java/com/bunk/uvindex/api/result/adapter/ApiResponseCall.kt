package com.bunk.uvindex.api.result.adapter

import com.bunk.uvindex.api.result.ApiResponse
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import javax.net.ssl.SSLPeerUnverifiedException

/**
 * Ensures thrown exceptions within Retrofit or OkHttp does not crash the app when using Coroutines.
 *
 * Wraps the response body into [Response.Success] - when successful or [Response.Failure] - on failure.
 */
internal class ApiResponseCall<S : Any>(
	private val call: Call<S>,
) : Call<ApiResponse<S>> {

	override fun enqueue(callback: Callback<ApiResponse<S>>) {
		call.enqueue(object : Callback<S> {
			override fun onResponse(call: Call<S>, response: Response<S>) {
				val apiResponse: ApiResponse<S> = if (response.isSuccessful) {
					val body: S? = response.body()
					if (body != null) {
						ApiResponse.Success(body)
					} else {
						ApiResponse.Failure("body is null")
					}
				} else ApiResponse.Failure("http error code: ${response.code()}")

				callback.onResponse(this@ApiResponseCall, Response.success(apiResponse))
			}

			override fun onFailure(call: Call<S>, throwable: Throwable) {
				if (throwable is SSLPeerUnverifiedException) {
					Timber.e(throwable) // serious problem, must be logged as error!
				}
				callback.onResponse(this@ApiResponseCall, Response.success(ApiResponse.Failure(throwable)))
			}
		})
	}

	override fun clone(): Call<ApiResponse<S>> = ApiResponseCall(call.clone())

	override fun execute(): Response<ApiResponse<S>> {
		throw UnsupportedOperationException("ResultCall doesn't support execute")
	}

	override fun isExecuted(): Boolean = call.isExecuted

	override fun cancel() = call.cancel()

	override fun isCanceled(): Boolean = call.isCanceled

	override fun request(): Request = call.request()

	override fun timeout(): Timeout = Timeout.NONE
}