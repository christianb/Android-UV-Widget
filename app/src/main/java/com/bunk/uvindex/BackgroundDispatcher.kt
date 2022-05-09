package com.bunk.uvindex

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext

/**
 * Background CoroutineDispatcher for Android applications which replaces both
 * [Dispatchers.Default] and [Dispatchers.IO].
 *
 * Made by Vasiliy Zukanov
 * https://github.com/techyourchance/android-coroutines-course/blob/master/app/src/main/java/com/techyourchance/coroutines/demonstrations/backgrounddispatcher/BackgroundDispatcher.kt
 */
object BackgroundDispatcher : CoroutineDispatcher() {

	private const val CORE_POOL_SIZE: Int = 3
	private const val MAXIMUM_POOL_SIZE: Int = Integer.MAX_VALUE
	private const val KEEP_ALIVE_TIME: Long = 60L
	private val KEE_ALIVE_TIME_UNIT: TimeUnit = TimeUnit.SECONDS

	private val threadFactory: ThreadFactory = object : ThreadFactory {
		private val threadCount = AtomicInteger(0)
		private val nextThreadName: String get() = "BackgroundDispatcher-worker-${threadCount.incrementAndGet()}"

		override fun newThread(runnable: java.lang.Runnable): Thread {
			return Thread(runnable, nextThreadName)
		}
	}

	private val threadPool = ThreadPoolExecutor(
		CORE_POOL_SIZE,
		MAXIMUM_POOL_SIZE,
		KEEP_ALIVE_TIME,
		KEE_ALIVE_TIME_UNIT,
		SynchronousQueue(),
		threadFactory
	)

	private val dispatcher: ExecutorCoroutineDispatcher = threadPool.asCoroutineDispatcher()

	override fun dispatch(context: CoroutineContext, block: Runnable) {
		dispatcher.dispatch(context, block)
	}

	/**
	 * Background CoroutineDispatcher for Android applications which replaces both
	 * [Dispatchers.Default] and [Dispatchers.IO].
	 */
	val Dispatchers.Background: BackgroundDispatcher get() = BackgroundDispatcher
}