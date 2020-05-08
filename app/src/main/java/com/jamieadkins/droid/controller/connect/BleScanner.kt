package com.jamieadkins.droid.controller.connect

import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import io.reactivex.Observable
import timber.log.Timber
import javax.inject.Inject

class BleScanner @Inject constructor(private val scanner: BluetoothLeScanner?) {

    fun scan(): Observable<ScanState> {
        return Observable.create { emitter ->
            val scanCallback = object : ScanCallback() {
                override fun onScanResult(callbackType: Int, result: ScanResult?) {
                    val device = result?.device
                    Timber.d("Device Name: ${device?.name} Device Address: ${device?.address}")
                    val found = device?.address?.let(ScanState::DroidFound) ?: ScanState.ScanFailed
                    emitter.onNext(found)
                }

                override fun onScanFailed(errorCode: Int) {
                    Timber.e("Scan Failed: $errorCode")
                    emitter.onNext(ScanState.ScanFailed)
                }
            }

            val filters = listOf(
                ScanFilter.Builder().setDeviceName("DROID").build()
            )
            val settings = ScanSettings.Builder().build()
            scanner?.startScan(filters, settings, scanCallback) ?: emitter.onNext(ScanState.ScanFailed)
            emitter.onNext(ScanState.Scanning)

            emitter.setCancellable { scanner?.stopScan(scanCallback) }
        }
    }
}