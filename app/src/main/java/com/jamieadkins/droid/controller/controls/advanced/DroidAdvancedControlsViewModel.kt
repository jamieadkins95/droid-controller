package com.jamieadkins.droid.controller.controls.advanced

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jamieadkins.droid.controller.addToComposite
import com.jamieadkins.droid.controller.connect.ConnectionState
import com.jamieadkins.droid.controller.connect.ConnectionStateMachine
import com.jamieadkins.droid.controller.controls.DroidConnectionManager
import com.jamieadkins.droid.controller.controls.DroidService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Provider

class DroidAdvancedControlsViewModel(
    private val droidConnectionManager: DroidConnectionManager,
    private val connectionStateMachine: ConnectionStateMachine
) : ViewModel(), DroidService by droidConnectionManager {

    private val compositeDisposable = CompositeDisposable()
    private val _connectionState = MutableLiveData<ConnectionState>()
    val connectionState: LiveData<ConnectionState> get() = _connectionState

    init {
        observeDroidConnection()
            .subscribe { state -> _connectionState.value = state }
            .addToComposite(compositeDisposable)
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    private fun observeDroidConnection(): Observable<ConnectionState> {
        return connectionStateMachine.observe()
            .distinctUntilChanged()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    class Factory @Inject constructor(
        private val droidConnectionManager: Provider<DroidConnectionManager>,
        private val stateMachine: Provider<ConnectionStateMachine>
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DroidAdvancedControlsViewModel(droidConnectionManager.get(), stateMachine.get()) as T
        }
    }

}