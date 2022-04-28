package com.bunk.uvindex.location

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.bunk.uvindex.permission.AppPermission
import com.bunk.uvindex.permission.PermissionHelper
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LocationRepositoryTest {

	private val applicationContext: Context = mockk()
	private val locationManager: LocationManager = mockk()

	private val classUnderTest = LocationRepository(applicationContext, locationManager)

	@Before
	fun setUp() {
		mockkStatic(PermissionHelper::class)
	}

	@After
	fun tearDown() {
		unmockkAll()
	}

	@Test
	fun `getLocation should return null when permissions not granted`() {
		every { ContextCompat.checkSelfPermission(applicationContext, any()) } returns PackageManager.PERMISSION_DENIED

		val actual = classUnderTest.getLocation()

		assertThat(actual).isNull()
	}

	@Test
	fun `getLocation should return Location`() {
		every { ContextCompat.checkSelfPermission(applicationContext, any()) } returns PackageManager.PERMISSION_GRANTED
		every { locationManager.allProviders } returns listOf("a-provider")
		val expected = Location("")
		every { locationManager.getLastKnownLocation("a-provider") } returns expected

		val actual = classUnderTest.getLocation()

		assertThat(actual).isEqualTo(expected)
		verify { ContextCompat.checkSelfPermission(applicationContext, AppPermission.ACCESS_COARSE_LOCATION.androidPermission) }
		verify { ContextCompat.checkSelfPermission(applicationContext, AppPermission.ACCESS_BACKGROUND_LOCATION.androidPermission) }
	}

	@Test
	fun `getLocation should most accurate location`() {
		every { ContextCompat.checkSelfPermission(applicationContext, any()) } returns PackageManager.PERMISSION_GRANTED
		every { locationManager.allProviders } returns listOf("berlin","london","paris")
		every { locationManager.getLastKnownLocation("berlin") } returns BERLIN
		every { locationManager.getLastKnownLocation("london") } returns LONDON
		every { locationManager.getLastKnownLocation("paris") } returns PARIS

		val actual = classUnderTest.getLocation()

		assertThat(actual).isEqualTo(BERLIN)
	}

	@Test
	fun `getLocation null when no provider is available`() {
		every { ContextCompat.checkSelfPermission(applicationContext, any()) } returns PackageManager.PERMISSION_GRANTED
		every { locationManager.allProviders } returns emptyList()

		val actual = classUnderTest.getLocation()

		assertThat(actual).isNull()
	}

	companion object {
		val BERLIN: Location = Location("").apply {
			latitude = 52.520008
			longitude = 13.404954
			accuracy = 2.0f
		}

		val LONDON: Location = Location("").apply {
			latitude = 51.509865
			longitude = -0.118092
			accuracy = 3.0f
		}

		val PARIS: Location = Location("").apply {
			latitude = 48.864716
			longitude = 2.349014
			accuracy = 4.0f
		}
	}
}