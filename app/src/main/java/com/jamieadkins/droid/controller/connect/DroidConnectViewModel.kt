package com.jamieadkins.droid.controller.connect

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jamieadkins.droid.controller.addToComposite
import com.jamieadkins.droid.controller.controls.ConnectionState
import com.jamieadkins.droid.controller.controls.DroidConnectionManager
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject
import javax.inject.Provider

class DroidConnectViewModel(
    private val droidConnectionManager: DroidConnectionManager,
    private val bleScanner: BleScanner,
    private val bluetoothEnabledChecker: BluetoothEnabledChecker,
    private val locationPermissionChecker: LocationPermissionChecker
) : ViewModel() {

    private val scanRequests = BehaviorSubject.create<Any>()
    private val compositeDisposable = CompositeDisposable()
    private val _scanState = MutableLiveData<ScanState>()
    val scanState: LiveData<ScanState> get() = _scanState

    init {
        scanRequests.switchMap { scanAndConnect() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { state -> _scanState.value = state }
            .addToComposite(compositeDisposable)
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    fun scan() {
        scanRequests.onNext(Any())
    }

    private fun scanAndConnect(): Observable<ScanState> {
        return bluetoothEnabledChecker.checkBluetoothEnabled()
            .switchIfEmpty(locationPermissionChecker.checkLocationPermission())
            .toObservable()
            .switchIfEmpty(bleScanner.scan())
            .distinctUntilChanged()
            .switchMap { state ->
                if (state is ScanState.DroidFound) {
                    droidConnectionManager.connect(state.address)
                    droidConnectionManager.observe()
                        .map { connection -> if (connection is ConnectionState.Connected) ScanState.Connected(state.address) else state }
                        .startWith(state)
                } else {
                    Observable.just(state)
                }
            }
            .distinctUntilChanged()
    }

    class Factory @Inject constructor(
        private val droidConnectionManager: Provider<DroidConnectionManager>,
        private val bleScanner: Provider<BleScanner>,
        private val bluetoothEnabledChecker: Provider<BluetoothEnabledChecker>,
        private val locationPermissionChecker: Provider<LocationPermissionChecker>
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DroidConnectViewModel(
                droidConnectionManager.get(), bleScanner.get(), bluetoothEnabledChecker.get(), locationPermissionChecker.get()
            ) as T
        }
    }

}