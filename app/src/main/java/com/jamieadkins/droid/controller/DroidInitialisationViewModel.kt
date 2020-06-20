package com.jamieadkins.droid.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jamieadkins.droid.controller.connect.ConnectionEvent
import com.jamieadkins.droid.controller.connect.ConnectionStateMachine
import com.jamieadkins.droid.controller.controls.DroidConnectionManager
import javax.inject.Inject
import javax.inject.Provider

/**
 * A dirty way to setup and teardown the BT stack. Should be scoped to the activity.
 */
class DroidInitialisationViewModel(
    private val droidConnectionManager: DroidConnectionManager,
    private val stateMachine: ConnectionStateMachine
) : ViewModel() {

    init {
        droidConnectionManager.initialise()
    }

    override fun onCleared() {
        super.onCleared()
        droidConnectionManager.onDestroy()
        stateMachine.postEvent(ConnectionEvent.Disconnected)
    }

    class Factory @Inject constructor(
        private val droidConnectionManager: Provider<DroidConnectionManager>,
        private val stateMachine: Provider<ConnectionStateMachine>
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DroidInitialisationViewModel(droidConnectionManager.get(), stateMachine.get()) as T
        }
    }

}