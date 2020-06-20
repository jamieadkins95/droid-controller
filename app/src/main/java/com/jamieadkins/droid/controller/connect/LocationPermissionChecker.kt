package com.jamieadkins.droid.controller.connect

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.jamieadkins.droid.controller.di.ApplicationContext
import io.reactivex.Maybe
import javax.inject.Inject

class LocationPermissionChecker @Inject constructor(@ApplicationContext private val context: Context) {

    fun checkLocationPermission(): Maybe<ConnectionEvent> {
        return Maybe.fromCallable {
            val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            ConnectionEvent.LocationPermissionNotGranted.takeIf { !granted }
        }
    }
}