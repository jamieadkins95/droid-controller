package com.jamieadkins.droid.controller.setup

import com.jamieadkins.droid.controller.addToComposite
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

class SetupPresenter @Inject constructor() : SetupContract.Presenter {

    private var view: SetupContract.View? = null
    private val compositeDisposable = CompositeDisposable()
    private val locationEnabled = BehaviorSubject.create<Boolean>()
    private val bluetoothEnabled = BehaviorSubject.create<Boolean>()

    override fun onAttach(newView: SetupContract.View) {
        view = newView

        Observable.combineLatest(locationEnabled, bluetoothEnabled, BiFunction { location: Boolean, bluetooth: Boolean -> SetupState(location, bluetooth) })
            .distinctUntilChanged()
            .subscribe { setup ->
                if (setup.location && setup.bluetooth) {
                    view?.setupComplete()
                } else {
                    if (!setup.bluetooth) view?.showBluetoothPrompt() else view?.hideBluetoothPrompt()
                    if (!setup.location) view?.showLocationPrompt() else view?.hideLocationPrompt()
                }
            }.addToComposite(compositeDisposable)
    }

    override fun onDetach() {
        compositeDisposable.clear()
        view = null
    }

    override fun onBluetoothEnabled(enabled: Boolean) = bluetoothEnabled.onNext(enabled)

    override fun onLocationPermissionGranted(granted: Boolean) = locationEnabled.onNext(granted)

    private data class SetupState(val location: Boolean, val bluetooth: Boolean)
}