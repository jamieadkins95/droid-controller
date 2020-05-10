package com.jamieadkins.droid.controller.controls

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jamieadkins.droid.controller.addToComposite
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Provider

class DroidConnectionViewModel(
    private val droidConnectionManager: DroidConnectionManager
) : ViewModel(), DroidService by droidConnectionManager {

    private val compositeDisposable = CompositeDisposable()
    private val _connectionState = MutableLiveData<ConnectionState>()
    val connectionState: LiveData<ConnectionState> get() = _connectionState

    init {
        droidConnectionManager.observe()
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
        private val droidConnectionManager: Provider<DroidConnectionManager>
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DroidConnectionViewModel(droidConnectionManager.get()) as T
        }
    }

}