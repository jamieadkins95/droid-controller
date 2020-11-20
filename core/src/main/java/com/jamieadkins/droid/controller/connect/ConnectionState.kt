package com.jamieadkins.droid.controller.connect

sealed class ConnectionState {

    data class Disconnected(val bluetoothDisabled: Boolean = false, val locationDisabled: Boolean = false) : ConnectionState()
    object Scanning : ConnectionState()
    data class Naming(val address: String) : ConnectionState()
    data class Connecting(val address: String) : ConnectionState()
    data class ConnectedWithoutHandshake(val address: String) : ConnectionState()
    data class Connected(val address: String, val doingSequencePlayback: Boolean = false) : ConnectionState()
}