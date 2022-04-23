package com.bunk.uvindex.api.result.adapter

import com.bunk.uvindex.api.result.ApiResponse
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Inspired by:
 * - https://stackoverflow.com/questions/56483235/how-to-create-a-call-adapter-for-suspending-functions-in-retrofit
 * - https://proandroiddev.com/create-retrofit-calladapter-for-coroutines-to-handle-response-as-states-c102440de37a
 */
internal class ApiResponseCallAdapterFactory : CallAdapter.Factory() {

	override fun get(
		returnType: Type,
		annotations: Array<out Annotation>,
		retrofit: Retrofit,
	): CallAdapter<*, *>? {
		// suspend functions wrap the response type in `Call`
		if (Call::class.java != getRawType(returnType)) {
			return null
		}

		// check first that the return type is `ParameterizedType`
		check(returnType is ParameterizedType) {
			"return type must be parameterized as Call<Result<<T>> or Call<Result<out T>>"
		}

		// get the response type inside the `Call` type
		val responseType: Type = getParameterUpperBound(0, returnType)

		// if the response type is not Result then we can't handle this type, so we return null
		if (ApiResponse::class.java != getRawType(responseType)) {
			return null
		}

		// the response type is Result and should be parameterized
		check(responseType is ParameterizedType) { "Response must be parameterized as Result<Foo> or Result<out Foo>" }

		val successBodyType = getParameterUpperBound(0, responseType)

		return ApiResponseCallAdapter<Any>(successBodyType)
	}
}