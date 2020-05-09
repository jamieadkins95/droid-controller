package com.jamieadkins.droid.controller.connect

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jamieadkins.droid.controller.addToComposite
import com.jamieadkins.droid.controller.controls.ConnectionState
import com.jamieadkins.droid.controller.controls.DroidManager
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Provider

class DroidConnectViewModel(
    private val droidManager: DroidManager,
    private val bleScanner: BleScanner,
    private val bluetoothEnabledChecker: BluetoothEnabledChecker,
    private val locationPermissionChecker: LocationPermissionChecker
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val _scanState = MutableLiveData<ScanState>()
    val scanState: LiveData<ScanState> get() = _scanState

    init {
        droidManager.initialise()
        bluetoothEnabledChecker.checkBluetoothEnabled()
            .switchIfEmpty(locationPermissionChecker.checkLocationPermission())
            .toObservable()
            .switchIfEmpty(bleScanner.scan())
            .distinctUntilChanged()
            .switchMap { state ->
                if (state is ScanState.DroidFound) {
                    droidManager.connect(state.address)
                    droidManager.observe()
                        .map { connection -> if (connection is ConnectionState.Connected) ScanState.Connected(state.address) else state }
                        .startWith(state)
                } else {
                    Observable.just(state)
                }
            }
            .distinctUntilChanged()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { state -> _scanState.value = state }
            .addToComposite(compositeDisposable)
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
        droidManager.onDestroy()
    }

    class Factory @Inject constructor(
        private val droidManager: Provider<DroidManager>,
        private val bleScanner: Provider<BleScanner>,
        private val bluetoothEnabledChecker: Provider<BluetoothEnabledChecker>,
        private val locationPermissionChecker: Provider<LocationPermissionChecker>
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DroidConnectViewModel(
                droidManager.get(), bleScanner.get(), bluetoothEnabledChecker.get(), locationPermissionChecker.get()
            ) as T
        }
    }

}