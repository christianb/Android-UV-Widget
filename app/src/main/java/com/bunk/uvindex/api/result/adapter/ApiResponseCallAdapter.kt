package com.bunk.uvindex.api.result.adapter

import com.bunk.uvindex.api.result.ApiResponse
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

internal class ApiResponseCallAdapter<S : Any>(
	private val type: Type,
) : CallAdapter<S, Call<ApiResponse<S>>> {

	override fun responseType(): Type = type

	override fun adapt(call: Call<S>): Call<ApiResponse<S>> = ApiResponseCall(call)
}