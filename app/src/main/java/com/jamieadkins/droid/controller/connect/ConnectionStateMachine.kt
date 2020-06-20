package com.jamieadkins.droid.controller.connect

import com.tinder.StateMachine
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

class ConnectionStateMachine {

    private val stateMachine: StateMachine<ConnectionState, ConnectionEvent, ConnectionSideEffect> = StateMachine.create {
        initialState(ConnectionState.Disconnected())
        state<ConnectionState.Disconnected> {
            on<ConnectionEvent.StartScan> { transitionTo(ConnectionState.Scanning) }
            on<ConnectionEvent.DroidSelected> { event -> transitionTo(ConnectionState.Connecting(event.address)) }
            on<ConnectionEvent.DroidNamed> { event -> transitionTo(ConnectionState.Connecting(event.address)) }
            on<ConnectionEvent.BluetoothDisabled> { transitionTo(ConnectionState.Disconnected(bluetoothDisabled = true)) }
            on<ConnectionEvent.LocationPermissionNotGranted> { transitionTo(ConnectionState.Disconnected(locationDisabled = true)) }
        }
        state<ConnectionState.Scanning> {
            on<ConnectionEvent.UnnamedDroidFound> { event -> transitionTo(ConnectionState.Naming(event.address)) }
            on<ConnectionEvent.NamedDroidFound> { event -> transitionTo(ConnectionState.Connecting(event.address)) }
            on<ConnectionEvent.DroidSelected> { event -> transitionTo(ConnectionState.Connecting(event.address)) }
            on<ConnectionEvent.BluetoothDisabled> { transitionTo(ConnectionState.Disconnected(bluetoothDisabled = true)) }
            on<ConnectionEvent.LocationPermissionNotGranted> { transitionTo(ConnectionState.Disconnected(locationDisabled = true)) }
            on<ConnectionEvent.Failure> { transitionTo(ConnectionState.Disconnected()) }
        }
        state<ConnectionState.Naming> {
            on<ConnectionEvent.DroidNamed> { event -> transitionTo(ConnectionState.Connecting(event.address), ConnectionSideEffect.ConnectToDroid(event.address)) }
            on<ConnectionEvent.DroidNamingCancelled> { transitionTo(ConnectionState.Disconnected()) }
            on<ConnectionEvent.BluetoothDisabled> { transitionTo(ConnectionState.Disconnected(bluetoothDisabled = true)) }
            on<ConnectionEvent.LocationPermissionNotGranted> { transitionTo(ConnectionState.Disconnected(locationDisabled = true)) }
            on<ConnectionEvent.Failure> { transitionTo(ConnectionState.Disconnected()) }
            on<ConnectionEvent.UserDisconnect> { transitionTo(ConnectionState.Disconnected()) }
            on<ConnectionEvent.Disconnected> { transitionTo(ConnectionState.Disconnected()) }
        }
        state<ConnectionState.Connecting> {
            on<ConnectionEvent.ConnectionSuccessful> { event ->transitionTo(ConnectionState.ConnectedWithoutHandshake(event.address)) }
            on<ConnectionEvent.BluetoothDisabled> { transitionTo(ConnectionState.Disconnected(bluetoothDisabled = true)) }
            on<ConnectionEvent.LocationPermissionNotGranted> { transitionTo(ConnectionState.Disconnected(locationDisabled = true)) }
            on<ConnectionEvent.Failure> { transitionTo(ConnectionState.Disconnected()) }
            on<ConnectionEvent.UserDisconnect> { transitionTo(ConnectionState.Disconnected()) }
            on<ConnectionEvent.Disconnected> { transitionTo(ConnectionState.Disconnected()) }
        }
        state<ConnectionState.ConnectedWithoutHandshake> {
            on<ConnectionEvent.HandshakeComplete> { event -> transitionTo(ConnectionState.Connected(event.address)) }
            on<ConnectionEvent.BluetoothDisabled> { transitionTo(ConnectionState.Disconnected(bluetoothDisabled = true)) }
            on<ConnectionEvent.LocationPermissionNotGranted> { transitionTo(ConnectionState.Disconnected(locationDisabled = true)) }
            on<ConnectionEvent.Failure> { transitionTo(ConnectionState.Disconnected()) }
            on<ConnectionEvent.UserDisconnect> { transitionTo(ConnectionState.Disconnected()) }
        }
        state<ConnectionState.Connected> {
            on<ConnectionEvent.SequenceStarted> { transitionTo(ConnectionState.Connected(this.address, true)) }
            on<ConnectionEvent.SequenceEnded> { transitionTo(ConnectionState.Connected(this.address, false)) }
            on<ConnectionEvent.BluetoothDisabled> { transitionTo(ConnectionState.Disconnected(bluetoothDisabled = true)) }
            on<ConnectionEvent.LocationPermissionNotGranted> { transitionTo(ConnectionState.Disconnected(locationDisabled = true)) }
            on<ConnectionEvent.Failure> { transitionTo(ConnectionState.Disconnected()) }
            on<ConnectionEvent.UserDisconnect> { transitionTo(ConnectionState.Disconnected()) }
            on<ConnectionEvent.Disconnected> { transitionTo(ConnectionState.Disconnected()) }
        }
        onTransition {
            val validTransition = it as? StateMachine.Transition.Valid ?: return@onTransition
            Timber.d("From: ${it.fromState}, To: ${it.toState}, Event: ${it.event}")
            currentState.onNext(validTransition.toState)
            validTransition.sideEffect?.let(sideEffects::onNext)
        }
    }

    private val currentState = BehaviorSubject.createDefault(stateMachine.state)
    private val sideEffects = PublishSubject.create<ConnectionSideEffect>()

    fun observe(): Observable<ConnectionState> = currentState.distinctUntilChanged()

    fun observeSideEffects(): Observable<ConnectionSideEffect> = sideEffects

    fun postEvent(connectionEvent: ConnectionEvent) = stateMachine.transition(connectionEvent)

}