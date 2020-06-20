package com.jamieadkins.droid.controller.connect

import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import com.jamieadkins.droid.controller.droid.DroidDao
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class BleScanner @Inject constructor(
    private val scanner: BluetoothLeScanner?,
    private val droidDao: DroidDao
) {

    fun scan(): Observable<ConnectionEvent> {
        return Observable.create<DroidScanResult> { emitter ->
            val scanCallback = object : ScanCallback() {
                override fun onScanResult(callbackType: Int, result: ScanResult?) {
                    val device = result?.device
                    Timber.d("Device Name: ${device?.name} Device Address: ${device?.address}")
                    val found = device?.address?.let(DroidScanResult::Found) ?: DroidScanResult.Error
                    emitter.onNext(found)
                }

                override fun onScanFailed(errorCode: Int) {
                    Timber.e("Scan Failed: $errorCode")
                    emitter.onNext(DroidScanResult.Error)
                }
            }

            val filters = listOf(
                ScanFilter.Builder().setDeviceName("DROID").build()
            )
            val settings = ScanSettings.Builder().build()
            scanner?.startScan(filters, settings, scanCallback) ?: emitter.onNext(DroidScanResult.Error)

            emitter.setCancellable { scanner?.stopScan(scanCallback) }
        }
            .distinctUntilChanged()
            .switchMap { scanResult ->
                if (scanResult is DroidScanResult.Found) {
                    val address = scanResult.address
                    droidDao.doesDroidExist(address)
                        .subscribeOn(Schedulers.io())
                        .map { count -> if (count > 0) ConnectionEvent.NamedDroidFound(address) else ConnectionEvent.UnnamedDroidFound(address) }
                        .toObservable()
                } else {
                    Observable.just(ConnectionEvent.Failure)
                }
            }
            .startWith(ConnectionEvent.StartScan)
    }

    sealed class DroidScanResult {
        data class Found(val address: String) : DroidScanResult()
        object Error : DroidScanResult()
    }
}