package com.jamieadkins.droid.controller.connect

import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class ConnectPresenter @Inject constructor() : ConnectContract.Presenter {

    private var view: ConnectContract.View? = null
    private val compositeDisposable = CompositeDisposable()

    override fun onAttach(newView: ConnectContract.View) {
        view = newView
        view?.showLoadingIndicator()
    }

    override fun onDetach() {
        compositeDisposable.clear()
        view = null
    }
}