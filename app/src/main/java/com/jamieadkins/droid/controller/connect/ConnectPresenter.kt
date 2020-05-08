package com.jamieadkins.droid.controller.connect

import com.jamieadkins.droid.controller.addToComposite
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class ConnectPresenter @Inject constructor(
    private val bleScanner: BleScanner,
    private val bluetoothEnabledChecker: BluetoothEnabledChecker,
    private val locationPermissionChecker: LocationPermissionChecker
) : ConnectContract.Presenter {

    private var view: ConnectContract.View? = null
    private val compositeDisposable = CompositeDisposable()

    override fun onAttach(newView: ConnectContract.View) {
        view = newView
        view?.showScanningIndicator()

        bluetoothEnabledChecker.checkBluetoothEnabled()
            .switchIfEmpty(locationPermissionChecker.checkLocationPermission())
            .toObservable()
            .switchIfEmpty(bleScanner.scan())
            .subscribe { state ->
                when (state) {
                    ScanState.BluetoothDisabled -> {
                        view?.disableScanButton()
                        view?.hideScanningIndicator()
                        view?.showBluetoothPrompt()
                        view?.hideLocationPrompt()
                    }
                    ScanState.LocationPermissionNotGranted -> {
                        view?.disableScanButton()
                        view?.hideScanningIndicator()
                        view?.showLocationPrompt()
                        view?.hideBluetoothPrompt()
                    }
                    ScanState.Scanning -> {
                        view?.disableScanButton()
                        view?.showScanningIndicator()
                        view?.hideBluetoothPrompt()
                        view?.hideLocationPrompt()
                    }
                    ScanState.ScanFailed -> {
                        view?.enableScanButton()
                        view?.hideScanningIndicator()
                        view?.hideBluetoothPrompt()
                        view?.hideLocationPrompt()
                    }
                    is ScanState.DroidFound -> {
                        view?.hideScanningIndicator()
                        view?.connectToDroid(state.address)
                        view?.hideBluetoothPrompt()
                        view?.hideLocationPrompt()
                    }
                }
            }
            .addToComposite(compositeDisposable)
    }

    override fun onDetach() {
        compositeDisposable.clear()
        view = null
    }
}