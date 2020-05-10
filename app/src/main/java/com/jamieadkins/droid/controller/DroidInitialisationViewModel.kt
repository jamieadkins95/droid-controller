package com.jamieadkins.droid.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jamieadkins.droid.controller.controls.DroidManager
import javax.inject.Inject
import javax.inject.Provider

/**
 * A dirty way to setup and teardown the BT stack. Should be scoped to the activity.
 */
class DroidInitialisationViewModel(
    private val droidManager: DroidManager
) : ViewModel() {

    init {
        droidManager.initialise()
    }

    override fun onCleared() {
        super.onCleared()
        droidManager.onDestroy()
    }

    class Factory @Inject constructor(
        private val droidManager: Provider<DroidManager>
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DroidInitialisationViewModel(droidManager.get()) as T
        }
    }

}