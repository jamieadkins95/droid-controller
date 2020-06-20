package com.jamieadkins.droid.controller.connect

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jamieadkins.droid.controller.addToComposite
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
    private val locationPermissionChecker: LocationPermissionChecker,
    private val connectionStateMachine: ConnectionStateMachine
) : ViewModel() {

    private val scanRequests = BehaviorSubject.create<Any>()
    private val compositeDisposable = CompositeDisposable()
    private val _scanState = MutableLiveData<ConnectionState>()
    val scanState: LiveData<ConnectionState> get() = _scanState

    init {
        scanRequests.switchMap { scanAndConnect() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { event -> connectionStateMachine.postEvent(event) }
            .addToComposite(compositeDisposable)

        connectionStateMachine.observe()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { state -> _scanState.value = state }
            .addToComposite(compositeDisposable)

        connectionStateMachine.observeSideEffects()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { sideEffect ->
                when (sideEffect) {
                    is ConnectionSideEffect.ConnectToDroid -> {
                        droidConnectionManager.connect(sideEffect.address)
                    }
                }
            }
            .addToComposite(compositeDisposable)
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    fun scan() {
        scanRequests.onNext(Any())
    }

    fun onDroidNamed(name: String, address: String, type: String) {
        connectionStateMachine.postEvent(ConnectionEvent.DroidNamed(address))
    }

    fun onDroidSelected(address: String) {
        connectionStateMachine.postEvent(ConnectionEvent.DroidSelected(address))
    }

    private fun scanAndConnect(): Observable<ConnectionEvent> {
        return bluetoothEnabledChecker.checkBluetoothEnabled()
            .switchIfEmpty(locationPermissionChecker.checkLocationPermission())
            .toObservable()
            .switchIfEmpty(bleScanner.scan())
            .distinctUntilChanged()
    }

    class Factory @Inject constructor(
        private val droidConnectionManager: Provider<DroidConnectionManager>,
        private val bleScanner: Provider<BleScanner>,
        private val bluetoothEnabledChecker: Provider<BluetoothEnabledChecker>,
        private val locationPermissionChecker: Provider<LocationPermissionChecker>,
        private val connectionStateMachine: Provider<ConnectionStateMachine>
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DroidConnectViewModel(
                droidConnectionManager.get(), bleScanner.get(), bluetoothEnabledChecker.get(), locationPermissionChecker.get(), connectionStateMachine.get()
            ) as T
        }
    }

}