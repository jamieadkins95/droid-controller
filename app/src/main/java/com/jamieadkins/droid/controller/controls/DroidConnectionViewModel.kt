package com.jamieadkins.droid.controller.controls

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jamieadkins.droid.controller.addToComposite
import com.jamieadkins.droid.controller.connect.ConnectionState
import com.jamieadkins.droid.controller.connect.ConnectionStateMachine
import com.jamieadkins.droid.controller.droid.Droid
import com.jamieadkins.droid.controller.droid.DroidDao
import com.jamieadkins.droid.controller.droid.DroidType
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Provider

class DroidConnectionViewModel(
    private val droidConnectionManager: DroidConnectionManager,
    private val connectionStateMachine: ConnectionStateMachine,
    private val droidDao: DroidDao
) : ViewModel(), DroidService by droidConnectionManager {

    private val compositeDisposable = CompositeDisposable()
    private val _connectionState = MutableLiveData<ConnectionState>()
    val connectionState: LiveData<ConnectionState> get() = _connectionState

    private val droidAddress = Transformations.map(connectionState) { state -> (state as? ConnectionState.Connected)?.address }
    private val distinctDroidAddress = Transformations.distinctUntilChanged(droidAddress)
    private val droid = Transformations.switchMap(distinctDroidAddress) { address -> address?.let(droidDao::getDroid) ?: MutableLiveData() }
    val headTurnEnabled: LiveData<Boolean> = Transformations.map(droid) { droid -> droid.type == DroidType.RUnit }
    val reactionsOffset: LiveData<Int> = Transformations.map(droid) { droid -> if (droid.type == DroidType.RUnit) 0 else 8 } // R Unit Droids are reactions 1-8, BB is 9-16
    val droidName: LiveData<String> = Transformations.map(droid) { droid -> droid.name }

    init {
        connectionStateMachine.observe()
            .distinctUntilChanged()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { state -> _connectionState.value = state }
            .addToComposite(compositeDisposable)
    }

    override fun onCleared() {
        compositeDisposable.clear()
        droidConnectionManager.disconnect()
        super.onCleared()
    }

    class Factory @Inject constructor(
        private val droidConnectionManager: Provider<DroidConnectionManager>,
        private val connectionStateMachine: Provider<ConnectionStateMachine>,
        private val droidDao: Provider<DroidDao>
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DroidConnectionViewModel(droidConnectionManager.get(), connectionStateMachine.get(), droidDao.get()) as T
        }
    }

}