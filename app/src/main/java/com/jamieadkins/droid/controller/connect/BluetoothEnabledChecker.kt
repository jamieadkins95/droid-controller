package com.jamieadkins.droid.controller.connect

import android.bluetooth.BluetoothAdapter
import io.reactivex.Maybe
import javax.inject.Inject

class BluetoothEnabledChecker @Inject constructor(private val adapter: BluetoothAdapter?) {

    fun checkBluetoothEnabled(): Maybe<ConnectionEvent> {
        return Maybe.fromCallable {
            val enabled = adapter?.isEnabled ?: false
            ConnectionEvent.BluetoothDisabled.takeIf { !enabled }
        }
    }
}