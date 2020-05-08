package com.jamieadkins.droid.controller.controls

import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

class DroidConnectionManager @Inject constructor() {

    private val connected = BehaviorSubject.createDefault(false)
    private val handshakeComplete = BehaviorSubject.createDefault(false)

    fun onDisconnected() {
        handshakeComplete.onNext(false)
        connected.onNext(false)
    }

    fun onHandshakeCompleted() {
        handshakeComplete.onNext(true)
    }

    fun onConnected() {
        connected.onNext(true)
    }

    fun observeCurrentState(): Observable<ConnectionState> {
        return Observable.combineLatest(
            connected,
            handshakeComplete,
            BiFunction { isConnected: Boolean, isHandshakeComplete: Boolean ->
                when {
                    isConnected && isHandshakeComplete -> ConnectionState.Connected
                    isConnected -> ConnectionState.ConnectedWithoutHandshake
                    else -> ConnectionState.Disconnected
                }
            }
        ).distinctUntilChanged()
    }

}