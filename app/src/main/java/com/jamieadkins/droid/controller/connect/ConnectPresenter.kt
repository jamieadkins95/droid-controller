package com.jamieadkins.droid.controller.connect

import com.jamieadkins.droid.controller.addToComposite
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class ConnectPresenter @Inject constructor(
    private val bleScanner: BleScanner
) : ConnectContract.Presenter {

    private var view: ConnectContract.View? = null
    private val compositeDisposable = CompositeDisposable()

    override fun onAttach(newView: ConnectContract.View) {
        view = newView
        view?.showLoadingIndicator()
        bleScanner.scan()
            .subscribe { state ->
                when (state) {
                    ScanState.Scanning -> view?.showLoadingIndicator()
                    ScanState.ScanFailed -> view?.hideLoadingIndicator()
                    is ScanState.DroidFound -> {
                        view?.hideLoadingIndicator()
                        view?.connectToDroid(state.address)
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