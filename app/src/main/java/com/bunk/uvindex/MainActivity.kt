package com.bunk.uvindex

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.bunk.uvindex.permission.AppPermission
import com.bunk.uvindex.permission.PermissionActivity
import com.bunk.uvindex.ui.theme.UvIndexTheme

class MainActivity : PermissionActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			UvIndexTheme {
				// A surface container using the 'background' color from the theme
				Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
					Column {
					}
				}
			}
		}

		requestPermission(
			AppPermission.ACCESS_COARSE_LOCATION,
			AppPermission.ACCESS_BACKGROUND_LOCATION
		)
	}
}