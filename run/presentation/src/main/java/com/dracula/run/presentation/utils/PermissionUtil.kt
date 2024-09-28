package com.dracula.run.presentation.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat

fun ComponentActivity.shouldShowLocationPermissionRotational(): Boolean {
	return shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
}

fun ComponentActivity.shouldShowNotificationPermissionRotational(): Boolean {
	return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
		shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)
	} else {
		true
	}
}

fun Context.hasPermission(permission: String): Boolean {
	return ContextCompat.checkSelfPermission(
		this,
		permission
	) == PERMISSION_GRANTED
}

fun Context.hasLocationPermission(): Boolean {
	return hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
}

fun Context.hasNotificationPermission(): Boolean {
	return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
		hasPermission(Manifest.permission.POST_NOTIFICATIONS)
	} else {
		true
	}
}
